package model;

import java.math.BigDecimal;
import java.util.List;

public class DespesaPorPessoa extends Despesa {

    private static final long serialVersionUID = 1L;

    public DespesaPorPessoa(
        String descricao,
        BigDecimal valor,
        List<Participante> participantes
    ) {

        super(descricao, valor, participantes);
    }

    @Override
    public BigDecimal calcularValorIndividual(
        Participante participante
    ) {

        if (participa(participante)) {
            return getValor();
        }

        return new BigDecimal("0.00");
    }

    @Override
    public String getTipo() {
        return "Por pessoa";
    }
}