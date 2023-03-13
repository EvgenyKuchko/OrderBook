import java.io.*;
import java.util.TreeSet;

public class ShareService {

    public TreeSet<Share> createBook() {
        return new TreeSet<>();
    }

    public void readFile(TreeSet<Share> orderBook, String filename) {
        File fileOutput = new File("output.txt");

        try {
            File fileInput = new File(filename);
            FileReader fr = new FileReader(fileInput);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                String[] words = line.split(",");
                switch (words[0]) {
                    case "u":
                        orderBook.add(setOrder(words, orderBook));
                        break;
                    case "q":
                        checkTypeOfQuery(orderBook, fileOutput, words);
                        break;
                    case "o":
                        changeSize(words, orderBook);
                        break;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Share setOrder(String[] words, TreeSet<Share> orderBook) {
        Share share = null;
        for (Share s : orderBook) {
            if (s.getPrice() == Integer.parseInt(words[1]) && s.getType().equals(words[3])) {
                share = s;
                share.setSize(share.getSize() + Integer.parseInt(words[2]));
                break;
            }
        }
        if (share == null) {
            share = new Share();
            share.setPrice(Integer.parseInt(words[1]));
            share.setSize(Integer.parseInt(words[2]));
            share.setType(words[3]);
        }
        return share;
    }

    private void checkTypeOfQuery(TreeSet<Share> orderBook, File file, String[] words) {
        switch (words[1]) {
            case "best_bid": {
                Share share = orderBook.stream()
                        .filter(p -> p.getType().equals("bid") && p.getSize() > 0)
                        .min(Share::compareTo)
                        .get();
                String line = share.getPrice() + "," + share.getSize();
                writeTextToTheFile(line, file);
                break;
            }
            case "best_ask": {
                Share share = orderBook.stream()
                        .filter(p -> p.getType().equals("ask") && p.getSize() > 0)
                        .max(Share::compareTo)
                        .get();
                String line = share.getPrice() + "," + share.getSize();
                writeTextToTheFile(line, file);
                break;
            }
            case "size": {
                Share share = null;
                for (Share s : orderBook) {
                    if (s.getPrice() == Integer.parseInt(words[2])) {
                        share = s;
                        break;
                    }
                }
                String line = "";
                if (share != null) {
                    line += String.valueOf(share.getSize());
                } else {
                    line += "0";
                }
                writeTextToTheFile(line, file);
                break;
            }
        }
    }

    private void writeTextToTheFile(String line, File file) {
        if (file.length() != 0) {
            line = "\n" + line;
        }
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            byte[] buffer = line.getBytes();
            fos.write(buffer, 0, buffer.length);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void changeSize(String[] words, TreeSet<Share> orderBook) {
        if (words[1].equals("buy")) {
            String finalType = "ask";
            orderBook.stream()
                    .filter(p -> p.getType().equals(finalType))
                    .filter(p -> p.getSize() >= Integer.parseInt(words[2]))
                    .max(Share::compareTo)
                    .ifPresent(p -> p.setSize(p.getSize() - Integer.parseInt(words[2])));
        } else if (words[1].equals("sell")) {
            String finalType1 = "bid";
            orderBook.stream()
                    .filter(p -> p.getType().equals(finalType1))
                    .filter(p -> p.getSize() >= Integer.parseInt(words[2]))
                    .min(Share::compareTo)
                    .ifPresent(p -> p.setSize(p.getSize() - Integer.parseInt(words[2])));
        } else {
            System.out.println("Bad format");
        }
    }
}