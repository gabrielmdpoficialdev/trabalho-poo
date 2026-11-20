package controller;

import model.*;
import persistence.Persistencia;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PlanejamentoController {

    private Planejamento planejamento;

    private final Persistencia persistencia;

    public PlanejamentoController(
        Planejamento planejamento,
        Persistencia persistencia
    ) {

        this.planejamento = planejamento == null
            ? new Planejamento()
            : planejamento;

        this.persistencia = persistencia;
    }

    public Planejamento getPlanejamento() {
        return planejamento;
    }

    public void adicionarParticipante(
        int codigo,
        String nome,
        BigDecimal orcamento
    ) {

        Participante participante =
            new Participante(codigo, nome, orcamento);

        planejamento.adicionarParticipante(participante);
    }

    public void alterarParticipante(
        int codigo,
        String nome,
        BigDecimal orcamento
    ) {

        planejamento.alterarParticipante(
            codigo,
            nome,
            orcamento
        );
    }

    public void excluirParticipante(int codigo) {

        planejamento.excluirParticipante(codigo);
    }

    public void adicionarDespesa(
        String descricao,
        BigDecimal valor,
        String tipo,
        List<Integer> codigos
    ) {

        List<Participante> selecionados =
            resolverParticipantes(codigos);

        Despesa despesa = criarDespesa(
            descricao,
            valor,
            tipo,
            selecionados
        );

        planejamento.adicionarDespesa(despesa);
    }

    public void alterarDespesa(
        int indice,
        String descricao,
        BigDecimal valor,
        String tipo,
        List<Integer> codigos
    ) {

        List<Participante> selecionados =
            resolverParticipantes(codigos);

        Despesa despesa = criarDespesa(
            descricao,
            valor,
            tipo,
            selecionados
        );

        planejamento.alterarDespesa(
            indice,
            despesa
        );
    }

    public void excluirDespesa(int indice) {

        planejamento.excluirDespesa(indice);
    }

    public void salvar(String caminho) throws IOException {

        persistencia.salvar(planejamento, caminho);
    }

    public void carregar(String caminho)
        throws IOException, ClassNotFoundException {

        Planejamento recuperado =
            persistencia.carregar(caminho);

        if (recuperado == null) {
            throw new IOException(
                "Arquivo sem planejamento."
            );
        }

        planejamento = recuperado;
    }

    private List<Participante> resolverParticipantes(
        List<Integer> codigos
    ) {

        if (codigos == null || codigos.isEmpty()) {

            throw new IllegalArgumentException(
                "Selecione pelo menos um participante."
            );
        }

        List<Participante> lista = new ArrayList<>();

        for (Integer codigo : codigos) {

            Participante p =
                planejamento.buscarParticipante(codigo);

            if (p == null) {

                throw new IllegalArgumentException(
                    "Participante não encontrado: " + codigo
                );
            }

            boolean repetido = lista.stream().anyMatch(
                x -> x.getCodigo() == codigo
            );

            if (repetido) {

                throw new IllegalArgumentException(
                    "Participante repetido."
                );
            }

            lista.add(p);
        }

        return lista;
    }

    private Despesa criarDespesa(
        String descricao,
        BigDecimal valor,
        String tipo,
        List<Participante> participantes
    ) {

        if ("Fixa".equalsIgnoreCase(tipo)) {

            return new DespesaFixa(
                descricao,
                valor,
                participantes
            );
        }

        if ("Por pessoa".equalsIgnoreCase(tipo)) {

            return new DespesaPorPessoa(
                descricao,
                valor,
                participantes
            );
        }

        throw new IllegalArgumentException(
            "Tipo de despesa inválido."
        );
    }
}