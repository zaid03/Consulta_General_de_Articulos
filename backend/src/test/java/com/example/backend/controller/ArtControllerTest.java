package com.example.backend.controller;

import com.example.backend.config.TestSecurityConfig;
import com.example.backend.dto.ArticleProjection;
import com.example.backend.sqlserver2.repository.ArtRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ArtController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class ArtControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArtRepository artRepository;

    @Test
    void fetchConsultaGeneral_returnsArticlesWhenFound() throws Exception {
        when(artRepository.findByENT(1, PageRequest.of(0, 20)))
            .thenReturn(List.of(mock(ArticleProjection.class), mock(ArticleProjection.class)));

        mockMvc.perform(get("/api/art/fetch-consulta-general/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(artRepository).findByENT(1, PageRequest.of(0, 20));
    }

    @Test
    void fetchConsultaGeneral_returnsNotFoundWhenEmpty() throws Exception {
        when(artRepository.findByENT(1, PageRequest.of(0, 20)))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/art/fetch-consulta-general/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Sin resultado")));

        verify(artRepository).findByENT(1, PageRequest.of(0, 20));
    }

    @Test
    void fetchConsultaGeneral_respectsPaginationPage0() throws Exception {
        when(artRepository.findByENT(1, PageRequest.of(0, 20)))
            .thenReturn(List.of(mock(ArticleProjection.class)));

        mockMvc.perform(get("/api/art/fetch-consulta-general/1?page=0")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).findByENT(1, PageRequest.of(0, 20));
    }

    @Test
    void fetchConsultaGeneral_respectsPaginationPage1() throws Exception {
        when(artRepository.findByENT(1, PageRequest.of(1, 20)))
            .thenReturn(List.of(mock(ArticleProjection.class)));

        mockMvc.perform(get("/api/art/fetch-consulta-general/1?page=1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).findByENT(1, PageRequest.of(1, 20));
    }

    @Test
    void fetchConsultaGeneral_differentEntityIds() throws Exception {
        when(artRepository.findByENT(2, PageRequest.of(0, 20)))
            .thenReturn(List.of(mock(ArticleProjection.class)));

        mockMvc.perform(get("/api/art/fetch-consulta-general/2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).findByENT(2, PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_returnsResultsWhenFound() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, "search", null, null, "todos", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?search=search")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, "search", null, null, "todos", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_returnsNotFoundWhenEmpty() throws Exception {
        Page<ArticleProjection> emptyPage = new PageImpl<>(List.of());
        when(artRepository.searchArticles(1, "xyz", null, null, "todos", PageRequest.of(0, 20)))
            .thenReturn(emptyPage);

        mockMvc.perform(get("/api/art/search/1?search=xyz")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Sin resultado")));

        verify(artRepository).searchArticles(1, "xyz", null, null, "todos", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_withSearchParameter() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, "bolt", null, null, "todos", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?search=bolt")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, "bolt", null, null, "todos", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_withAfacodParameter() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, null, "FAM", null, "todos", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?afacod=FAM")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, null, "FAM", null, "todos", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_withAsucodParameter() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, null, null, "SUB", "todos", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?asucod=SUB")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, null, null, "SUB", "todos", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_withAllParameters() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, "bolt", "FAM", "SUB", "nobloqueado", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?search=bolt&afacod=FAM&asucod=SUB&bloqueado=nobloqueado")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, "bolt", "FAM", "SUB", "nobloqueado", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_defaultBloqueadoToTodos() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, null, null, null, "todos", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, null, null, null, "todos", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_withBloqueadoFilterTodos() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, null, null, null, "todos", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?bloqueado=todos")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, null, null, null, "todos", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_withBloqueadoFilterBloqueado() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, null, null, null, "bloqueado", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?bloqueado=bloqueado")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, null, null, null, "bloqueado", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_withBloqueadoFilterNobloqueado() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, null, null, null, "nobloqueado", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?bloqueado=nobloqueado")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, null, null, null, "nobloqueado", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_respectsPaginationPage0() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, null, null, null, "todos", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?page=0")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, null, null, null, "todos", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_respectsPaginationPage2() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(1, null, null, null, "todos", PageRequest.of(2, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?page=2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, null, null, null, "todos", PageRequest.of(2, 20));
    }

    @Test
    void searchArticles_differentEntityIds() throws Exception {
        Page<ArticleProjection> page = new PageImpl<>(List.of(mock(ArticleProjection.class)));
        when(artRepository.searchArticles(2, null, null, null, "todos", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/2")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(2, null, null, null, "todos", PageRequest.of(0, 20));
    }

    @Test
    void searchArticles_multipleResultsPagination() throws Exception {
        ArticleProjection article1 = mock(ArticleProjection.class);
        ArticleProjection article2 = mock(ArticleProjection.class);
        Page<ArticleProjection> page = new PageImpl<>(List.of(article1, article2));
        when(artRepository.searchArticles(1, "test", null, null, "todos", PageRequest.of(0, 20)))
            .thenReturn(page);

        mockMvc.perform(get("/api/art/search/1?search=test")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(artRepository).searchArticles(1, "test", null, null, "todos", PageRequest.of(0, 20));
    }
}
