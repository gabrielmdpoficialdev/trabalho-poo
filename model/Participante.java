package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Participante implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int codigo;
    private String nome;
    private BigDecimal orcamento;

    public Participante(int codigo, String nome, BigDecimal orcamento) {

        if (codigo <= 0) {
            throw new IllegalArgumentException(
                "O código deve ser positivo."
            );
        }

        this.codigo = codigo;
        setNome(nome);
        setOrcamento(orcamento);
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getOrcamento() {
        return orcamento;
    }

    public void setNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                "O nome não pode ficar vazio."
            );
        }

        this.nome = nome.trim();
    }

    public void setOrcamento(BigDecimal orcamento) {

        if (orcamento == null ||
            orcamento.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                "O orçamento deve ser maior ou igual a zero."
            );
        }

        this.orcamento = orcamento.setScale(
            2, RoundingMode.HALF_UP
        );
    }

    @Override
    public String toString() {
        return codigo + " - " + nome;
    }
}