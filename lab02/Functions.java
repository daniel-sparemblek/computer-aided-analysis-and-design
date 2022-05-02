public class Functions {
    private final int num_of_function;
    private int num_of_calls = 0;

    public Functions(int num) {
        num_of_function = num;
    }

    public double run(double[] x) {
        return switch (num_of_function) {
            case (1) -> function1(x);
            case (2) -> function2(x);
            case (3) -> function3(x);
            case (4) -> function4(x);
            case (6) -> function6(x);
            default -> 0;
        };
    }

    private double function1(double[] x) {
        num_of_calls++;
        return (100*Math.pow((x[1] - Math.pow(x[0], 2)), 2)) + Math.pow((1 - x[0]), 2);
    }

    private double function2(double[] x) {
        num_of_calls++;
        return Math.pow((x[0]-4), 2) + 4 * Math.pow((x[1] - 2), 2);
    }

    private double function3(double[] x) { //TODO Change?
        num_of_calls++;
        double sum = 0;
        if (x.length == 1)
            sum = Math.pow((x[0]-3), 2);
        else
            for(int i = 0; i < x.length; i++)
              sum += Math.pow((x[i]-i), 2);
        return sum;
    }

    private double function4(double[] x) {
        num_of_calls++;
        return Math.abs((x[0] - x[1])*(x[0]+x[1])) + Math.sqrt(Math.pow(x[0], 2) + Math.pow(x[1], 2));
    }

    private double function6(double[] x) {
        num_of_calls++;
        double numerator = Math.pow(Math.sin(Math.pow(x[0], 2) - Math.pow(x[1], 2)), 2) - 0.5;
        double denomenator = Math.pow((1 + 0.001 * (Math.pow(x[0], 2) + Math.pow(x[1], 2))), 2);
        return 0.5 + numerator / denomenator;
    }

    public int getNum_of_calls() {
        return num_of_calls;
    }

    public void setNum_of_calls(int num_of_calls) {
        this.num_of_calls = num_of_calls;
    }
}
