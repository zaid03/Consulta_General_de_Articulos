import { Component, HostListener} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule, JsonPipe } from '@angular/common';
import { DatePipe } from '@angular/common';
import { SidebarComponent } from '../sidebar/sidebar.component';

import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';
import { environment } from '../../environments/environment';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-consulta-general-articulos',
  standalone: true,
  imports: [ CommonModule ,FormsModule, SidebarComponent],
  templateUrl: './consulta-general-articulos.component.html',
  styleUrls: ['./consulta-general-articulos.component.css']
})
export class ConsultaGeneralArticulosComponent {
  //3 dots menu 
  showMenu = false;
  toggleMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.showMenu = !this.showMenu;
  }

  @HostListener('document:click')
  closeMenu(): void {
    this.showMenu = false;
  }

  //global variables
  private entcod: number | null = null;
  private eje: number | null = null;
  articulos: any[] = [];
  private backupArticulos: any[] = [];
  page = 0;
  pageSize = 20;
  public Math = Math;

  constructor(private http: HttpClient, private router: Router) {}

  isLoading: boolean = false;
  articuloSuccess: string = '';
  articuloError: string = '';
  ngOnInit(): void{
    this.limpiarMessages();
    const entidad = sessionStorage.getItem('Entidad');
    const eje = sessionStorage.getItem('EJERCICIO'); 

    if (entidad) {const parsed = JSON.parse(entidad); this.entcod = parsed.ENTCOD;}
    if (eje) {const parsed = JSON.parse(eje); this.eje = parsed.eje;}

    if (!entidad || this.entcod === null || !eje || this.eje === null) {
      sessionStorage.clear();
      alert('Debes iniciar sesión para acceder a esta página.');
      this.router.navigate(['/login']);
      return;
    }

    this.fetchArticulos();
  }

  fetchArticulos() {
    this.isLoading = true;
    this.limpiarMessages();

    this.http.get<any>(`${environment.backendUrl}/api/`).subscribe({
      next: (res) => {
        this.isLoading = false;
        this.articulos = res;
        this.backupArticulos = [...this.articulos];
        this.updatePagination;
      },
      error: (err) => {
        this.articuloError = err.error.error || err.error;
        this.isLoading = false;
      }
    })
  }
  private updatePagination(): void {const total = this.totalPages;
    if (total === 0) {this.page = 0; return;}
    if (this.page >= total) {this.page = total - 1;}
  }
  get paginatedArticulos(): any[] {if (!this.articulos || this.articulos.length === 0) return []; const start = this.page * this.pageSize; return this.articulos.slice(start, start + this.pageSize);}
  get totalPages(): number {return Math.max(1, Math.ceil((this.articulos?.length ?? 0) / this.pageSize));}
  prevPage(): void {if (this.page > 0) this.page--;}
  nextPage(): void {if (this.page < this.totalPages - 1) this.page++;}
  goToPage(event: any): void {const inputPage = Number(event.target.value); if (inputPage >= 1 && inputPage <= this.totalPages) {this.page = inputPage - 1;}}

  //main table functions
  sortField: 'afacod' | 'asucod' | 'artcod' | 'artdes' | 'artuni' | 'artref' | 'artblo' | 'afades' | null = null;
  sortColumn: string = '';
  sortDirection: 'asc' | 'desc' = 'asc';
  toggleSort(column: string) {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.applySort();
    this.page = 0;
    this.updatePagination();
  }

  private applySort(): void {
  if (!this.sortColumn) return;
  
  // Helper to pad numbers for proper sorting
  const parseValue = (val: string) => {
    return val.replace(/(\d+)/g, (match) => {
      return match.padStart(20, '0');
    });
  };

  this.articulos.sort((a, b) => {
    const aValue = parseValue((a[this.sortColumn] ?? '').toString().toUpperCase());
    const bValue = parseValue((b[this.sortColumn] ?? '').toString().toUpperCase());
    
    const comparison = aValue.localeCompare(bValue);
    return this.sortDirection === 'asc' ? comparison : -comparison;
  });
}
  private startX: number = 0;
  private startWidth: number = 0;
  private resizingColIndex: number | null = null;
  startResize(event: MouseEvent, colIndex: number) {
    this.resizingColIndex = colIndex;
    this.startX = event.pageX;
    const th = (event.target as HTMLElement).parentElement as HTMLElement;
    this.startWidth = th.offsetWidth;

    document.addEventListener('mousemove', this.onResizeMove);
    document.addEventListener('mouseup', this.stopResize);
  }

  onResizeMove = (event: MouseEvent) => {
    if (this.resizingColIndex === null) return;
    const table = document.querySelector('.main-table') as HTMLTableElement;
    if (!table) return;
    const th = table.querySelectorAll('th')[this.resizingColIndex] as HTMLElement;
    if (!th) return;
    const diff = event.pageX - this.startX;
    th.style.width = (this.startWidth + diff) + 'px';
  };

  stopResize = () => {
    document.removeEventListener('mousemove', this.onResizeMove);
    document.removeEventListener('mouseup', this.stopResize);
    this.resizingColIndex = null;
  };

  //misc
  limpiarMessages() {
    this.articuloError = '';
    this.articuloSuccess = '';
  }
}
