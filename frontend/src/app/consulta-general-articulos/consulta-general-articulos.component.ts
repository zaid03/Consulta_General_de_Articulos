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

    this.http.get<any>(`${environment.backendUrl}/api/art/fetch-consulta-general/${this.entcod}?page=${this.page}`).subscribe({
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
  }
  get paginatedArticulos(): any[] {return this.articulos || [];}
  get totalPages(): number {return Math.max(1, Math.ceil((this.articulos?.length ?? 0) / this.pageSize));}
  prevPage() {
    if (this.page == 0) {return;}
    this.page = this.page - 1;
    this.fetchArticulos();
    return;
  }
  nextPage() {
    this.page = this.page + 1;
    this.fetchArticulos();
    return;
  }

  isBloqueado(artblo: number): string {
    if (artblo == 1) return 'Sí'
    return 'No';
  }

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

  DownloadPDF() {
    this.limpiarMessages();

    const source = this.paginatedArticulos;
    if (!source?.length) {
      this.articuloError = 'No hay datos para exportar.';
      return;
    }

    const rows = source.map((row: any) => ({
      afacod: row.afacod ?? '',
      asucod: row.asucod ?? '',
      artcod: row.artcod ?? '',
      artdes: row.artdes ?? '',
      artuni: row.artuni ?? '',
      artref: row.artref ?? '',
      artblo: this.isBloqueado(row.artblo) ?? ''
    }));

    const columns = [
      { header: 'Familia', dataKey: 'afacod' },
      { header: 'Subfamilia', dataKey: 'asucod'},
      { header: 'Código', dataKey: 'artcod'},
      { header: 'Descripción', dataKey: 'artdes'},
      { header: 'Estocaje', dataKey: 'artuni'},
      { header: 'Referencia Universal', dataKey: 'artref'},
      { header: 'Bloqueo', dataKey: 'artblo'}
    ];

    const doc = new jsPDF({ orientation: 'landscape', unit: 'pt', format: 'a4' });
    doc.setFont('helvetica', 'normal');
    doc.setFontSize(14);
    doc.text('Consulta analîtica de familias', 40, 40);

    autoTable(doc, {
      startY: 60,
      head: [columns.map(col => col.header)],
      body: rows.map(row => columns.map(col => row[col.dataKey as keyof typeof row] ?? '')),
      styles: { font: 'helvetica', fontSize: 10, cellPadding: 6 },
      headStyles: { fillColor: [240, 240, 240], textColor: 33, fontStyle: 'bold' },
      columnStyles: {
        afacod: { cellWidth: 10 },
        asucod: { cellWidth: 10 },
        artcod: { cellWidth: 10 },
        artdes: { cellWidth: 50 },
        artuni: { cellWidth: 10 },
        artref: { cellWidth: 15 },
        artblo: { cellWidth: 8 }
      }
    });

    doc.save('Consulta_general_de_articulos.pdf');
  }

  downloadExcel() {
    this.limpiarMessages();
    const rows = this.paginatedArticulos;
    if (!rows || rows.length === 0) {
      this.articuloError = 'No hay datos para exportar.';
      return;
    }
  
    const exportRows = rows.map(row => ({
      afacod: row.afacod ?? '',
      asucod: row.asucod ?? '',
      artcod: row.artcod ?? '',
      artdes: row.artdes ?? '',
      artuni: row.artuni ?? '',
      artref: row.artref ?? '',
      artblo: this.isBloqueado(row.artblo) ?? ''
    }));
  
    const worksheet = XLSX.utils.aoa_to_sheet([]);
    XLSX.utils.sheet_add_aoa(worksheet, [['Consulta general de articulos']], { origin: 'A1' });
    worksheet['!merges'] = [{ s: { r: 0, c: 0 }, e: { r: 0, c: 3 } }];
    XLSX.utils.sheet_add_aoa(worksheet, [['Familias', 'Subfamilia', 'Código', 'Descripción', 'Estocaje', 'Referencia universal', 'Bloqueo']], { origin: 'A2' });
    XLSX.utils.sheet_add_json(worksheet, exportRows, { origin: 'A3', skipHeader: true });

    worksheet['!cols'] = [
      { wch: 10 },
      { wch: 10 },
      { wch: 10 },
      { wch: 50 },
      { wch: 10 },
      { wch: 15 },
      { wch: 8 }
    ];
  
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Articulos');
    const buffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    saveAs(
      new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' }),
      'Consulta_general_de_articulos.xlsx'
    );
  }

  //detail grid functions
  selectedArticulo: any = null;
  articuloDetailError: string = '';
  articuloDetailSuccess: string = '';
  isUpdating: boolean = false;
  showDetails(articulo: any) {
    this.limpiarMessages();
    this.selectedArticulo = articulo;
    this.tempArticulo = articulo;
  }

  closeDetails() {
    this.selectedArticulo = null;
    this.tempArticulo = [];
    // this.activeDetailTab = null;
    this.limpiarMessages();
  }

  closeDetailsSure() {if (this.isUpdate) {return;} 
    else {this.closeDetails();}
  }

  kestvir: number | null = null;
  calculateKEstVir(artuni: number, artsol: number, artrec: number) {
    if (!artuni || !artsol || !artrec) {return;}
    this.kestvir = artuni - artsol + artrec 
    return this.kestvir;
  }
  calculateKvalExi(artuni: number, artpmp: number) {
    if (!artuni || !artpmp) {return;}
    return artuni * artpmp;
  }
  calculateKUniSol(artuni: number, artsol: number, artrec: number, artmin: number, artopt: number): number {
    const kestvir = this.calculateKEstVir(artuni, artsol, artrec);
    if (!kestvir || kestvir < artmin) {return 0;}
    this.kestvir = artopt - kestvir;
    return this.kestvir;
  }

  tempArticulo: any = {};
  isUpdate: boolean = false;
  backupData: any = [];
  modificar() {
    this.isUpdate = true;
    this.backupData = this.selectedArticulo ? { ...this.selectedArticulo } : {};
  }

  cancelar() {
    this.isUpdate = false;
    this.tempArticulo = { ...this.backupData };
  }

  // updateSuccess() {
  //   this.isUpdate = false;
  //   this.allowToUpdate = false;
  // }

  allowToUpdate: boolean = false;
  // isUpdateAllowed(afacod: string, afades: string) {
  //   if (this.allowToUpdate) {
  //     // this.updateFamilia(afacod, afades);
  //   } else {
  //     return;
  //   }
  // }

  
  // articuloDetailSuccess: any[] = [];
  activeDetailTab: 'subfamilia' | null = null;
  showSubfamiliasGrid: boolean = false;

  //misc
  limpiarMessages() {
    this.articuloError = '';
    this.articuloSuccess = '';
    this.articuloDetailError = '';
    this.articuloDetailSuccess = '';
  }
}
