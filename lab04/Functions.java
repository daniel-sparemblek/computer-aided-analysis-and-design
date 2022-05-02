public class Functions {
    private final int num_of_function;
    private int num_of_calls = 0;

    public Functions(int num) {
        num_of_function = num;
    }

    public double run(double[] x) {
        return switch (num_of_function) {
            case (1) -> function1(x);
            case (3) -> function3(x);
            case (6) -> function6(x);
            case (7) -> function7(x);
            default -> 0;
        };
    }

    private double function1(double[] x) {
        num_of_calls++;
        return 100*Math.pow((x[1] - Math.pow(x[0], 2)), 2) + Math.pow((1 - x[0]), 2);
//        return Math.pow(x[0], 2) + Math.pow(x[1], 2);
    }

    private double function3(double[] x) {
        num_of_calls++;
        double sum = 0;
        for(int i = 0; i < x.length; i++){
            sum += Math.pow((x[i] - i), 2);
        }
        return sum;
    }

    private double function6(double[] x) {
        num_of_calls++;
        double sum = 0;
        for (double v : x) {
            sum += Math.pow(v, 2);
        }
        return 0.5 + (Math.pow(Math.sin(Math.sqrt(sum)), 2) - 0.5) / Math.pow(1+0.001 * sum, 2);
    }

    private double function7(double[] x) {
        num_of_calls++;
        double sum = 0;
        for (double v : x) {
            sum += Math.pow(v, 2);
        }
        return Math.pow(Math.pow(sum, 2), 0.25) * (1 + Math.pow(Math.sin(50 * Math.pow(Math.pow(sum, 2), 0.1)), 2));
    }

    public int getNum_of_calls() {
        return num_of_calls;
    }

    public void setNum_of_calls(int num_of_calls) {
        this.num_of_calls = num_of_calls;
    }
}
