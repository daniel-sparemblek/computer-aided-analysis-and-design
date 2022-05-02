public class Functions {
    private int num_of_function = 0;
    private int num_of_calls = 0;
    private double[] free;
    private double[] connected;

    public Functions(int num) {
        num_of_function = num;
    }

    public double run(double[] x) {
        return switch (num_of_function) {
            case (1) -> function1(x);
            case (2) -> function2(x);
            case (3) -> function3(x);
            case (4) -> function4(x);
            default -> 0;
        };
    }

    public double[] get_gradient(double[] x) {
        return switch (num_of_function) {
            case (1) -> function1_gradient(x);
            case (2) -> function2_gradient(x);
            case (3) -> function3_gradient(x);
//            case (4) -> function4_gradient(x);
            default -> new double[1];
        };
    }

    private double function1(double[] x) {
        num_of_calls++;
        return 100*Math.pow((x[1] - Math.pow(x[0], 2)), 2) + Math.pow((1 - x[0]), 2);
    }

    public double function_U(double[] x, int t) {
        num_of_calls++;
        if(num_of_function == 1)
            return 100*Math.pow((x[1] - Math.pow(x[0], 2)), 2) + Math.pow((1 - x[0]), 2) - 1./t * (Math.log(x[1] - x[0]) +
                Math.log(2 - x[0]));
        else if(num_of_function == 2)
            return Math.pow((x[0] - 4), 2) + 4 * Math.pow((x[1] - 2), 2) - 1./t * (Math.log(x[1] - x[0]) + Math.log(2 - x[0]));
        else if(num_of_function == 4)
            return Math.pow((x[0] - 3), 2) + Math.pow(x[1], 2) - 1./t * (Math.log(3-x[0]-x[1])+Math.log(3+1.5*x[0]-x[1])) + t * Math.pow((x[1] - 1), 2);
        else throw new NullPointerException();
    }

    private double[] function1_gradient(double[] x) {
        num_of_calls++;
        double[] res = new double[2];
        res[0] = -400 * x[0] * (x[1] - Math.pow(x[0], 2)) - 2 * (1 - x[0]);
        res[1] = 200 * (x[1] - Math.pow(x[0], 2));
        return res;
    }

    private double[] function2_gradient(double[] x) {
        num_of_calls++;
        double[] res = new double[2];
        res[0] = 2*(x[0]-4);
        res[1] = 8*(x[1]-2);
        return res;
    }

    private double[] function3_gradient(double[] x) {
        num_of_calls++;
        double[] res = new double[2];
        res[0] = 2*(x[0]-2);
        res[1] = 2*(x[1]+3);
        return res;
    }

    private double function2(double[] x) {
        num_of_calls++;
        return Math.pow((x[0] - 4), 2) + 4 * Math.pow((x[1] - 2), 2);
    }

    private double function3(double[] x) {
        num_of_calls++;
        return Math.pow((x[0] - 2), 2) + Math.pow((x[1] + 3), 2);
    }

    private double function4(double[] x) {
        num_of_calls++;
        return Math.pow((x[0] - 3), 2) + Math.pow(x[1], 2);
    }

    public int getNum_of_calls() {
        return num_of_calls;
    }

    public void setNum_of_calls(int num_of_calls) {
        this.num_of_calls = num_of_calls;
    }

    public Functions define(double[] free, double[] connected) {
        this.free = free;
        this.connected = connected;
        return this;
    }

    public boolean check_explicit(double[] point) {
        if (num_of_function == 1 || num_of_function == 2) {
            for (double p : point) {
                if (p > 100 || p < -100)
                    return false;
            }
        } else return false;
        return true;
    }

    public boolean check_implicit(double[] point) {
        if (num_of_function == 1 || num_of_function == 2) {
            if (point[1] - point[0] < 0)
                return false;
            if (2 - point[0] < 0)
                return false;
        } else if (num_of_function == 4) {
            if(3 - point[0] - point[1] < 0)
                return false;
            if(3 + 1.5 * point[0] - point[1] < 0)
                return false;
        }
        return true;
    }

    public double find_new_point(double[] point) {
        int t1 = 0;
        int t2 = 0;
        if(3 - point[0] - point[1] < 0)
            t1 = 1;
        if(3 + 1.5 * point[0] - point[1] < 0)
            t2 = 1;
        return -t1*(3 - point[0] - point[1]) - t2*(3 + 1.5 * point[0] - point[1]);
    }

    public double[][] get_hesse(double[] x) {
        if (num_of_function == 1) {
            double[][] tmp = new double[2][2];
            tmp[0][0] = 2 * (600*Math.pow(x[0], 2) - 200 * x[1] + 1);
            tmp[0][1] = -400 * x[0];
            tmp[1][0] = -400 * x[0];
            tmp[1][1] = 200;
            return tmp;
        } else if (num_of_function == 2) {
            double[][] tmp = new double[2][2];
            tmp[0][0] = 2;
            tmp[0][1] = 0;
            tmp[1][0] = 0;
            tmp[1][1] = 1;
            return tmp;
        }
        else throw new NullPointerException();
    }
}
