package com.example.projetotestesjunit5.business;

import java.util.List;

import com.example.projetotestesjunit5.infrastructure.PessoaRepository;
import com.example.projetotestesjunit5.infrastructure.entity.Pessoa;
import com.example.projetotestesjunit5.infrastructure.exception.BusinessException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class PessoaServiceTest {
    @InjectMocks
    PessoaService service;

    @Mock
    PessoaRepository repository;

    Pessoa pessoa;

    @BeforeEach
    public void setUp(){
        pessoa = new Pessoa("Erineldo Filho", "01234567890", "QA Engineer", 22, "Ceará", "Rua Doze de Agosto", 688);
    }

    @Test
    void deveBuscarPessoasPorCPFComSucesso(){
        when(repository.findPessoa(pessoa.getCpf())).thenReturn(Collections.singletonList(pessoa));

        List<Pessoa> pessoas = service.buscaPessoasPorCpf(pessoa.getCpf());

        assertEquals(Collections.singletonList(pessoa), pessoas);
        verify(repository).findPessoa(pessoa.getCpf());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void naoDeveChamaroRepositoryCasoParametroCPFNulo(){
        final BusinessException e = assertThrows(BusinessException.class, () -> {
            service.buscaPessoasPorCpf((null));
        });

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Erro ao buscar pessoas por cpf = null"));
        assertThat(e.getCause(), notNullValue());
        assertThat(e.getCause().getMessage(), is("Cpf é obrigatório!"));
        verifyNoInteractions(repository);
    }

    @Test
    void deveAcionarExceptionQuandoRepositoryFalhar(){
        when(repository.findPessoa(pessoa.getCpf())).thenThrow(new RuntimeException(("Falha ao buscar pessoas por cpf!")));

        final BusinessException e = assertThrows(BusinessException.class, () -> {
            service.buscaPessoasPorCpf((pessoa.getCpf()));
        });

        MatcherAssert.assertThat(e.getMessage(), is(format("Erro ao buscar pessoas por cpf = %s", pessoa.getCpf())));
        MatcherAssert.assertThat(e.getCause().getClass(), is(RuntimeException.class));
        MatcherAssert.assertThat(e.getCause().getMessage(), is("Falha ao buscar pessoas por cpf!"));
        verify(repository).findPessoa(pessoa.getCpf());
        verifyNoMoreInteractions(repository);
    }

}
