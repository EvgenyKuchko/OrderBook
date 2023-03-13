import java.util.TreeSet;

public class Main {
    public static void main(String[] args) {
        ShareService shareService = new ShareService();
        TreeSet<Share> orderBook = shareService.createBook();
        shareService.readFile(orderBook, "input.txt");
    }
}