public class Test{

    Scanner scan = new Scanner("input1.txt");
    Parser  parser = new Parser(scan);

    public static void main(String[] args) {
        Test test = new Test();
        test.run();
    }

    private void run(){
         parser.Parse();
    }
}