package persistence;

import model.Planejamento;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

public class PersistenciaArquivo implements Persistencia {

    @Override
    public void salvar(
        Planejamento planejamento,
        String caminho
    ) throws IOException {

        if (planejamento == null) {
            throw new IllegalArgumentException(
                "Planejamento inválido."
            );
        }

        Path arquivo = Path.of(caminho);

        Path pasta = arquivo.getParent();

        if (pasta != null) {
            Files.createDirectories(pasta);
        }

        try (
            ObjectOutputStream saida =
                new ObjectOutputStream(
                    new FileOutputStream(caminho)
                )
        ) {

            saida.writeObject(planejamento);
        }
    }

    @Override
    public Planejamento carregar(
        String caminho
    ) throws IOException, ClassNotFoundException {

        try (
            ObjectInputStream entrada =
                new ObjectInputStream(
                    new FileInputStream(caminho)
                )
        ) {

            Object objeto = entrada.readObject();

            if (!(objeto instanceof Planejamento)) {

                throw new IOException(
                    "O arquivo não contém um planejamento válido."
                );
            }

            return (Planejamento) objeto;
        }
    }
}