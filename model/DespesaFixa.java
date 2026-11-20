package model;

import java.math.BigDecimal;
import java.util.List;

public class DespesaFixa extends Despesa {

    private static final long serialVersionUID = 1L;

    public DespesaFixa(
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

        if (!participa(participante)) {
            return new BigDecimal("0.00");
        }

        List<Participante> ordenados =
            participantesOrdenados();

        long centavos = valorEmCentavos();

        long base = centavos / ordenados.size();

        long resto = centavos % ordenados.size();

        for (int i = 0; i < ordenados.size(); i++) {

            if (ordenados.get(i).getCodigo()
                    == participante.getCodigo()) {

                long parte = base + (i < resto ? 1 : 0);

                return BigDecimal.valueOf(parte, 2);
            }
        }

        return new BigDecimal("0.00");
    }

    @Override
    public String getTipo() {
        return "Fixa";
    }
}