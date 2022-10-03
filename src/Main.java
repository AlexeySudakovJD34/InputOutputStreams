import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    public static void main(String[] args) {
        // Создаем папки и файлы, лог выполнения операций сохраняем в строку
        StringBuilder log = new StringBuilder();
        dirCreator("Games", log);
        dirCreator("Games/res", log);
        dirCreator("Games/src", log);
        dirCreator("Games/savegames", log);
        dirCreator("Games/temp", log);
        fileCreator("Games/temp", "temp.txt", log);
        dirCreator("Games/src/main", log);
        fileCreator("Games/src/main", "Main.java", log);
        fileCreator("Games/src/main", "Utils.java", log);
        dirCreator("Games/src/test", log);
        dirCreator("Games/res/drawables", log);
        dirCreator("Games/res/vectors", log);
        dirCreator("Games/res/icons", log);

        // записываем лог в temp.txt
        try (FileWriter writer = new FileWriter("Games/temp/temp.txt", false)) {
            writer.write(log.toString());
            writer.flush();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

        // создаем 3 экземпляра GameProgress
        GameProgress gameProgress1 = new GameProgress(100, 3, 1, 0);
        GameProgress gameProgress2 = new GameProgress(70, 3, 3, 120);
        GameProgress gameProgress3 = new GameProgress(65, 4, 4, 200);

        // сохраняем 3 экземпляра GameProgress
        saveGame("Games/savegames/save1.dat", gameProgress1);
        saveGame("Games/savegames/save2.dat", gameProgress2);
        saveGame("Games/savegames/save3.dat", gameProgress3);

        // создаем список файлов для архивации
        List<String> listForZIP = new ArrayList<>();
        listForZIP.add("Games/savegames/save1.dat");
        listForZIP.add("Games/savegames/save2.dat");
        listForZIP.add("Games/savegames/save3.dat");

        // архивируем файлы
        zipFiles("Games/savegames/zip.zip", listForZIP);

        // удаляем исходники заархивированных файлов
        for (String fileForDel : listForZIP) {
            File file = new File(fileForDel);
            file.delete();
        }

//        // разархивирование файлов в указанный каталог
//        openZip("Games/savegames/zip.zip", "Games/savegames");

//        // извлечем сохраненный экземпляр GameProgress из save2.dat
//        GameProgress respawn = openProgress("Games/savegames/save2.dat");
//        System.out.println(respawn.toString());
    }

    public static void dirCreator(String dirPath, StringBuilder log) {
        File dir = new File(dirPath);
        Date date = new Date();
        if (dir.mkdir()) {
            log.append(date + " : Создание каталога " + dirPath + " успешно" + "\n");
        } else {
            log.append(date + " : Создание каталога " + dirPath + " не успешно" + "\n");
        }
    }

    public static void fileCreator(String dirPath, String fileName, StringBuilder log) {
        File file = new File(dirPath, fileName);
        Date date = new Date();
        try {
            if (file.createNewFile()) {
                log.append(date + " : Создание файла " + fileName + " успешно" + "\n");
            } else {
                log.append(date + " : Создание файла " + fileName + " не успешно" + "\n");
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static void saveGame(String filePath, GameProgress gameProgress) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(gameProgress);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static void zipFiles(String zipPath, List<String> list) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipPath))) {
            for (String filePath : list) {
                FileInputStream fin = new FileInputStream(filePath);
                File file = new File(filePath);
                ZipEntry entry = new ZipEntry(file.getName());
                zout.putNextEntry(entry);
                byte[] buffer = new byte[fin.available()];
                fin.read(buffer);
                fin.close();
                zout.write(buffer);
                zout.closeEntry();
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static void openZip(String zipPath, String unzipPath) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(zipPath))) {
            ZipEntry entry;
            String name;
            String newName;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                newName = unzipPath + "/" + name;
                FileOutputStream fout = new FileOutputStream(newName);
                for (int i = zin.read(); i != -1; i = zin.read()) {
                    fout.write(i);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static GameProgress openProgress(String filePath) {
        GameProgress gameProgress = null;
        try (FileInputStream fin = new FileInputStream(filePath);
                ObjectInputStream oin = new ObjectInputStream(fin)) {
            gameProgress = (GameProgress) oin.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }
}