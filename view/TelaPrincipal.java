package view;

import controller.PlanejamentoController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TelaPrincipal extends JFrame {

    private final PlanejamentoController controller;

    private final NumberFormat moeda =
        NumberFormat.getCurrencyInstance(
            new Locale("pt", "BR")
        );

    private final DefaultTableModel modeloParticipantes =
        new DefaultTableModel(
            new Object[]{
                "Código",
                "Nome",
                "Orçamento",
                "Total",
                "Situação"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

    private final DefaultTableModel modeloDespesas =
        new DefaultTableModel(
            new Object[]{
                "Descrição",
                "Tipo",
                "Valor",
                "Participantes"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

    private final JTable tabelaParticipantes =
        new JTable(modeloParticipantes);

    private final JTable tabelaResumo =
        new JTable(modeloParticipantes);

    private final JTable tabelaDespesas =
        new JTable(modeloDespesas);

    private final JLabel lblTotal =
        new JLabel("Custo total: R$ 0,00");

    public TelaPrincipal(
        PlanejamentoController controller
    ) {

        this.controller = controller;

        setTitle(
            "Cabe no bolso - Planejador de despesas compartilhadas"
        );

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(950, 600);

        setLocationRelativeTo(null);

        JTabbedPane abas = new JTabbedPane();

        abas.addTab(
            "Participantes",
            criarPainelParticipantes()
        );

        abas.addTab(
            "Despesas",
            criarPainelDespesas()
        );

        abas.addTab(
            "Resumo",
            criarPainelResumo()
        );

        add(abas, BorderLayout.CENTER);

        JPanel rodape = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        JButton salvar = new JButton("Salvar");

        JButton carregar = new JButton("Carregar");

        salvar.addActionListener(e -> salvar());

        carregar.addActionListener(e -> carregar());

        rodape.add(salvar);
        rodape.add(carregar);

        add(rodape, BorderLayout.SOUTH);

        atualizarTabelas();
    }

    private JPanel criarPainelParticipantes() {

        JPanel painel = new JPanel(
            new BorderLayout(8, 8)
        );

        painel.add(
            new JScrollPane(tabelaParticipantes),
            BorderLayout.CENTER
        );

        JPanel botoes = new JPanel();

        JButton adicionar = new JButton("Adicionar");

        JButton alterar = new JButton("Alterar selecionado");

        JButton excluir = new JButton("Excluir selecionado");

        adicionar.addActionListener(
            e -> adicionarParticipante()
        );

        alterar.addActionListener(
            e -> alterarParticipante()
        );

        excluir.addActionListener(
            e -> excluirParticipante()
        );

        botoes.add(adicionar);
        botoes.add(alterar);
        botoes.add(excluir);

        painel.add(botoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelDespesas() {

        JPanel painel = new JPanel(
            new BorderLayout(8, 8)
        );

        painel.add(
            new JScrollPane(tabelaDespesas),
            BorderLayout.CENTER
        );

        JPanel botoes = new JPanel();

        JButton adicionar = new JButton("Adicionar");

        JButton alterar = new JButton("Alterar selecionada");

        JButton excluir = new JButton("Excluir selecionada");

        adicionar.addActionListener(
            e -> adicionarDespesa()
        );

        alterar.addActionListener(
            e -> alterarDespesa()
        );

        excluir.addActionListener(
            e -> excluirDespesa()
        );

        botoes.add(adicionar);
        botoes.add(alterar);
        botoes.add(excluir);

        painel.add(botoes, BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelResumo() {

        JPanel painel = new JPanel(
            new BorderLayout(8, 8)
        );

        painel.add(
            new JScrollPane(tabelaResumo),
            BorderLayout.CENTER
        );

        lblTotal.setFont(
            lblTotal.getFont().deriveFont(
                Font.BOLD,
                16f
            )
        );

        painel.add(lblTotal, BorderLayout.SOUTH);

        return painel;
    }

    private void adicionarParticipante() {

        JTextField codigo = new JTextField();

        JTextField nome = new JTextField();

        JTextField orcamento = new JTextField();

        Object[] campos = {
            "Código:",
            codigo,
            "Nome:",
            nome,
            "Orçamento:",
            orcamento
        };

        int resposta = JOptionPane.showConfirmDialog(
            this,
            campos,
            "Novo participante",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (resposta == JOptionPane.OK_OPTION) {

            executar(() -> {

                controller.adicionarParticipante(
                    Integer.parseInt(codigo.getText().trim()),
                    nome.getText(),
                    decimal(orcamento.getText())
                );

            });
        }
    }

    private void alterarParticipante() {

        int linha =
            tabelaParticipantes.getSelectedRow();

        if (linha < 0) {

            aviso("Selecione um participante.");

            return;
        }

        int codigo = (int)
            modeloParticipantes.getValueAt(linha, 0);

        Participante p =
            controller.getPlanejamento()
                .buscarParticipante(codigo);

        JTextField nome =
            new JTextField(p.getNome());

        JTextField orcamento =
            new JTextField(
                p.getOrcamento().toPlainString()
            );

        Object[] campos = {
            "Nome:",
            nome,
            "Orçamento:",
            orcamento
        };

        int resposta = JOptionPane.showConfirmDialog(
            this,
            campos,
            "Alterar participante",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (resposta == JOptionPane.OK_OPTION) {

            executar(() -> {

                controller.alterarParticipante(
                    codigo,
                    nome.getText(),
                    decimal(orcamento.getText())
                );

            });
        }
    }

    private void excluirParticipante() {

        int linha =
            tabelaParticipantes.getSelectedRow();

        if (linha < 0) {

            aviso("Selecione um participante.");

            return;
        }

        int codigo = (int)
            modeloParticipantes.getValueAt(linha, 0);

        executar(() ->
            controller.excluirParticipante(codigo)
        );
    }

    private void adicionarDespesa() {

        formularioDespesa(-1);
    }

    private void alterarDespesa() {

        int linha = tabelaDespesas.getSelectedRow();

        if (linha < 0) {

            aviso("Selecione uma despesa.");

            return;
        }

        formularioDespesa(linha);
    }

    private void formularioDespesa(int indice) {

        if (controller.getPlanejamento()
                .getParticipantes().isEmpty()) {

            aviso(
                "Cadastre participantes antes das despesas."
            );

            return;
        }

        Despesa existente = null;

        if (indice >= 0) {

            existente = controller.getPlanejamento()
                .getDespesas().get(indice);
        }

        JTextField descricao = new JTextField(
            existente == null
                ? ""
                : existente.getDescricao()
        );

        JTextField valor = new JTextField(
            existente == null
                ? ""
                : existente.getValor().toPlainString()
        );

        JComboBox<String> tipo =
            new JComboBox<>(
                new String[]{"Fixa", "Por pessoa"}
            );

        if (existente != null) {

            tipo.setSelectedItem(
                existente.getTipo()
            );
        }

        JPanel selecao = new JPanel(
            new GridLayout(0, 1)
        );

        List<JCheckBox> checks = new ArrayList<>();

        for (Participante p :
                controller.getPlanejamento()
                    .getParticipantes()) {

            boolean marcado = existente != null &&
                existente.getParticipantes().stream()
                    .anyMatch(
                        x -> x.getCodigo() == p.getCodigo()
                    );

            JCheckBox check =
                new JCheckBox(p.toString(), marcado);

            check.putClientProperty(
                "codigo",
                p.getCodigo()
            );

            checks.add(check);

            selecao.add(check);
        }

        JPanel campos = new JPanel(
            new GridLayout(0, 2, 5, 5)
        );

        campos.add(new JLabel("Descrição:"));
        campos.add(descricao);

        campos.add(new JLabel("Valor:"));
        campos.add(valor);

        campos.add(new JLabel("Tipo:"));
        campos.add(tipo);

        campos.add(new JLabel("Participantes:"));
        campos.add(new JScrollPane(selecao));

        int resposta = JOptionPane.showConfirmDialog(
            this,
            campos,
            indice < 0
                ? "Nova despesa"
                : "Alterar despesa",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (resposta == JOptionPane.OK_OPTION) {

            List<Integer> codigos = new ArrayList<>();

            for (JCheckBox check : checks) {

                if (check.isSelected()) {

                    codigos.add(
                        (Integer) check.getClientProperty(
                            "codigo"
                        )
                    );
                }
            }

            executar(() -> {

                if (indice < 0) {

                    controller.adicionarDespesa(
                        descricao.getText(),
                        decimal(valor.getText()),
                        (String) tipo.getSelectedItem(),
                        codigos
                    );

                } else {

                    controller.alterarDespesa(
                        indice,
                        descricao.getText(),
                        decimal(valor.getText()),
                        (String) tipo.getSelectedItem(),
                        codigos
                    );
                }

            });
        }
    }

    private void excluirDespesa() {

        int linha = tabelaDespesas.getSelectedRow();

        if (linha < 0) {

            aviso("Selecione uma despesa.");

            return;
        }

        executar(() ->
            controller.excluirDespesa(linha)
        );
    }

    private void salvar() {

        try {

            controller.salvar(
                "dados/planejamento.dat"
            );

            aviso("Planejamento salvo com sucesso.");

        } catch (IOException ex) {

            erro(
                "Não foi possível salvar: " +
                ex.getMessage()
            );
        }
    }

    private void carregar() {

        try {

            controller.carregar(
                "dados/planejamento.dat"
            );

            atualizarTabelas();

            aviso("Planejamento carregado com sucesso.");

        } catch (
            IOException | ClassNotFoundException ex
        ) {

            erro(
                "Não foi possível carregar o arquivo. " +
                "O planejamento atual foi preservado.\n" +
                ex.getMessage()
            );
        }
    }

    private BigDecimal decimal(String texto) {
        String normalizado = texto.trim();

        if (normalizado.contains(",")) {
            normalizado = normalizado
                .replace(".", "")
                .replace(",", ".");
    }
        return new BigDecimal(normalizado);
    }

    private void executar(Runnable operacao) {

        try {

            operacao.run();

            atualizarTabelas();

        } catch (RuntimeException ex) {

            erro(
                ex.getMessage() == null
                    ? "Entrada inválida."
                    : ex.getMessage()
            );
        }
    }

    private void atualizarTabelas() {

        modeloParticipantes.setRowCount(0);

        for (Participante p :
                controller.getPlanejamento()
                    .getParticipantes()) {

            BigDecimal total =
                controller.getPlanejamento()
                    .calcularTotalIndividual(p);

            String situacao;

            if (controller.getPlanejamento()
                    .estaDentroDoOrcamento(p)) {

                situacao = "Dentro do orçamento";

            } else {

                situacao = "Excedido em " +
                    moeda.format(
                        controller.getPlanejamento()
                            .calcularExcessoOrcamento(p)
                    );
            }

            modeloParticipantes.addRow(
                new Object[]{
                    p.getCodigo(),
                    p.getNome(),
                    moeda.format(p.getOrcamento()),
                    moeda.format(total),
                    situacao
                }
            );
        }

        modeloDespesas.setRowCount(0);

        for (Despesa d :
                controller.getPlanejamento()
                    .getDespesas()) {

            String nomes = d.getParticipantes()
                .stream()
                .map(Participante::getNome)
                .reduce(
                    (a, b) -> a + ", " + b
                )
                .orElse("");

            modeloDespesas.addRow(
                new Object[]{
                    d.getDescricao(),
                    d.getTipo(),
                    moeda.format(d.getValor()),
                    nomes
                }
            );
        }

        lblTotal.setText(
            "Custo total: " +
            moeda.format(
                controller.getPlanejamento()
                    .calcularCustoTotal()
            )
        );
    }

    private void aviso(String mensagem) {

        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Cabe no bolso",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void erro(String mensagem) {

        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Erro",
            JOptionPane.ERROR_MESSAGE
        );
    }
}