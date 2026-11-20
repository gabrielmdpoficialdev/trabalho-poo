import controller.PlanejamentoController;
import javax.swing.SwingUtilities;
import model.Planejamento;
import persistence.PersistenciaArquivo;
import view.TelaPrincipal;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Planejamento planejamento =
                new Planejamento();

            PersistenciaArquivo persistencia =
                new PersistenciaArquivo();

            PlanejamentoController controller =
                new PlanejamentoController(
                    planejamento,
                    persistencia
                );

            TelaPrincipal tela =
                new TelaPrincipal(controller);

            tela.setVisible(true);
        });
    }
}