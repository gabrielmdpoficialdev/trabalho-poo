package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class Despesa implements Serializable {

    private static final long serialVersionUID = 1L;

    private String descricao;
    private BigDecimal valor;
    private List<Participante> participantes;

    protected Despesa(
        String descricao,
        BigDecimal valor,
        List<Participante> participantes
    ) {

        setDescricao(descricao);
        setValor(valor);
        setParticipantes(participantes);
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public List<Participante> getParticipantes() {
        return new ArrayList<>(participantes);
    }

    public void setDescricao(String descricao) {

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                "A descrição não pode ficar vazia."
            );
        }

        this.descricao = descricao.trim();
    }

    public void setValor(BigDecimal valor) {

        if (valor == null ||
            valor.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                "O valor deve ser maior que zero."
            );
        }

        this.valor = valor.setScale(
            2, RoundingMode.HALF_UP
        );
    }

    public void setParticipantes(
        List<Participante> participantes
    ) {

        if (participantes == null ||
            participantes.isEmpty()) {

            throw new IllegalArgumentException(
                "Selecione pelo menos um participante."
            );
        }

        List<Participante> copia = new ArrayList<>();

        for (Participante p : participantes) {

            if (p == null) {
                throw new IllegalArgumentException(
                    "Participante inválido."
                );
            }

            boolean repetido = copia.stream().anyMatch(
                x -> x.getCodigo() == p.getCodigo()
            );

            if (repetido) {
                throw new IllegalArgumentException(
                    "Há participante repetido."
                );
            }

            copia.add(p);
        }

        this.participantes = copia;
    }

    public abstract BigDecimal calcularValorIndividual(
        Participante participante
    );

    public abstract String getTipo();

    protected boolean participa(Participante participante) {

        return participante != null &&
            participantes.stream().anyMatch(
                p -> p.getCodigo() == participante.getCodigo()
            );
    }

    protected List<Participante> participantesOrdenados() {

        return participantes.stream()
            .sorted(
                Comparator.comparingInt(
                    Participante::getCodigo
                )
            )
            .toList();
    }

    protected long valorEmCentavos() {

        return valor.movePointRight(2).longValueExact();
    }
}