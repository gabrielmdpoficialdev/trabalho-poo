package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Planejamento implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Participante> participantes =
        new ArrayList<>();

    private List<Despesa> despesas =
        new ArrayList<>();

    public List<Participante> getParticipantes() {
        return new ArrayList<>(participantes);
    }

    public List<Despesa> getDespesas() {
        return new ArrayList<>(despesas);
    }

    public void adicionarParticipante(
        Participante participante
    ) {

        if (participante == null) {
            throw new IllegalArgumentException(
                "Participante inválido."
            );
        }

        if (buscarParticipante(
                participante.getCodigo()) != null) {

            throw new IllegalArgumentException(
                "Código já cadastrado."
            );
        }

        participantes.add(participante);
    }

    public void alterarParticipante(
        int codigo,
        String nome,
        BigDecimal orcamento
    ) {

        Participante p = exigirParticipante(codigo);

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                "Nome inválido."
            );
        }

        if (orcamento == null ||
            orcamento.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                "Orçamento inválido."
            );
        }

        p.setNome(nome);
        p.setOrcamento(orcamento);
    }

    public void excluirParticipante(int codigo) {

        boolean vinculado = despesas.stream().anyMatch(
            d -> d.getParticipantes().stream().anyMatch(
                p -> p.getCodigo() == codigo
            )
        );

        if (vinculado) {
            throw new IllegalStateException(
                "Participante possui despesas vinculadas."
            );
        }

        Participante p = exigirParticipante(codigo);

        participantes.remove(p);
    }

    public void adicionarDespesa(Despesa despesa) {

        validarVinculos(despesa);

        despesas.add(despesa);
    }

    public void alterarDespesa(
        int indice,
        Despesa novaDespesa
    ) {

        if (indice < 0 || indice >= despesas.size()) {
            throw new IllegalArgumentException(
                "Despesa não encontrada."
            );
        }

        validarVinculos(novaDespesa);

        despesas.set(indice, novaDespesa);
    }

    public void excluirDespesa(int indice) {

        if (indice < 0 || indice >= despesas.size()) {
            throw new IllegalArgumentException(
                "Despesa não encontrada."
            );
        }

        despesas.remove(indice);
    }

    public BigDecimal calcularTotalIndividual(
        Participante participante
    ) {

        BigDecimal total = BigDecimal.ZERO;

        for (Despesa d : despesas) {

            total = total.add(
                d.calcularValorIndividual(participante)
            );
        }

        return total.setScale(2);
    }

    public BigDecimal calcularCustoTotal() {

        BigDecimal total = BigDecimal.ZERO;

        for (Despesa d : despesas) {

            for (Participante p : d.getParticipantes()) {

                total = total.add(
                    d.calcularValorIndividual(p)
                );
            }
        }

        return total.setScale(2);
    }

    public BigDecimal calcularExcessoOrcamento(
        Participante participante
    ) {

        BigDecimal excesso =
            calcularTotalIndividual(participante)
                .subtract(participante.getOrcamento());

        if (excesso.compareTo(BigDecimal.ZERO) > 0) {
            return excesso;
        }

        return new BigDecimal("0.00");
    }

    public boolean estaDentroDoOrcamento(
        Participante participante
    ) {

        return calcularTotalIndividual(participante)
            .compareTo(participante.getOrcamento()) <= 0;
    }

    public Participante buscarParticipante(int codigo) {

        return participantes.stream()
            .filter(p -> p.getCodigo() == codigo)
            .findFirst()
            .orElse(null);
    }

    private Participante exigirParticipante(int codigo) {

        Participante p = buscarParticipante(codigo);

        if (p == null) {
            throw new IllegalArgumentException(
                "Participante não encontrado."
            );
        }

        return p;
    }

    private void validarVinculos(Despesa despesa) {

        if (despesa == null) {
            throw new IllegalArgumentException(
                "Despesa inválida."
            );
        }

        for (Participante p : despesa.getParticipantes()) {

            if (buscarParticipante(p.getCodigo()) == null) {
                throw new IllegalArgumentException(
                    "Participante não cadastrado."
                );
            }
        }
    }
}