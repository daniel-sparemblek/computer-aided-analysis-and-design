import static java.lang.System.exit;

public class Search {

    private Functions f;

    public Search(Functions f) {
        this.f = f;
    }

    private double[] unimodal(double h, double point) {
        double l = point - h, r = point + h;
        double m = point;
        double fl, fm, fr;
        int step = 1;
        double[] tmp = {point};
        fm = f.run(tmp);
        tmp[0] = l;
        fl = f.run(tmp);
        tmp[0] = r;
        fr = f.run(tmp);

        if(fm < fr && fm < fl)
            return new double[0]; // TODO Sort if this
        else if(fm > fr)
            do {
                l = m;
                m = r;
                fm = fr;
                r = point + h * (step *= 2);
                tmp[0] = r;
                fr = f.run(tmp);
            } while(fm > fr);
        else
            do {
                r = m;
                m = l;
                fm = fl;
                l = point - h * (step *= 2);
                tmp[0] = l;
                fl = f.run(tmp);
            } while(fm > fl);

        return new double[] {l, r};
    }

    public double[] golden_ratio(double e, double ... param) {
        double[] interval;
        if (param.length == 1)
            interval = unimodal(1, param[0]);
        else interval = param;
        System.out.println("Golden ratio:");
        double[] res = golden_ratio_search(0, param, interval[0], interval[1], e);
        System.out.println("Result: " + Math.round(((res[0] + res[1])/2.) * 100d) / 100d);
        return res;
    }

    private double[] golden_ratio_search(int optimize_index, double[] X0, double a, double b, double e) {
        double k = 0.5 * (Math.sqrt(5) - 1); // Defined golden ration

        double[] tmp_param = copy_values(X0);
        int iteration = 0;
        double c = b - k * (b - a);
        double d = a + k * (b - a);
        tmp_param[optimize_index] = c;
        double fc = f.run(tmp_param);
        tmp_param[optimize_index] = d;
        double fd = f.run(tmp_param);
        while((b - a) > e) {
            //print_golden_ratio(a, b, c, d, ++iteration, optimize_index, X0);
            if(fc < fd) {
                b = d;
                d = c;
                c = b - k * (b - a);
                fd = fc;
                tmp_param[optimize_index] = c;
                fc = f.run(tmp_param);
            } else {
                a = c;
                c = d;
                d = a + k * (b - a);
                fc = fd;
                tmp_param[optimize_index] = d;
                fd = f.run(tmp_param);
            }
        }
        return new double[] {a, b};
    }

    private void print_golden_ratio(double a, double b, double c, double d, int it, int opt_index, double[] X0) {
        System.out.println("Iteration " + it);
        System.out.println("------------------");
        double[] tmp_param = copy_values(X0);
        tmp_param[opt_index] = a;
        System.out.println("a: " + a + " - f(a): " + f.run(tmp_param));
        tmp_param[opt_index] = c;
        System.out.println("c: " + c + " - f(c): " + f.run(tmp_param));
        tmp_param[opt_index] = d;
        System.out.println("d: " + d + " - f(d): " + f.run(tmp_param));
        tmp_param[opt_index] = b;
        System.out.println("b: " + b + " - f(b): " + f.run(tmp_param));
    }

    public void simplex(double alpha, double beta, double gamma, double epsilon, double sigma, double[] point, double move) {
        System.out.println("Simplex search: ");
        int dimensions = point.length;
        double[][] X = generate_points(point, move, dimensions + 1);
        double[] F = new double[X.length];
        double[] Xc;
        int counter = 0;
        do {
            for (int i = 0; i < X.length; i++) {
                F[i] = f.run(X[i]);
            }
            int[] tmp = find_largest_smallest(F);
            int h = tmp[0];
            int l = tmp[1];
            Xc = centroid(X, h);
            double[] Xr = reflexion(X[h], Xc, alpha);

            if (f.run(Xr)<F[l]) {
                double[] Xe = expansion(Xr, Xc, gamma);
                if (f.run(Xe)<F[l])
                    X[h] = Xe;
                else
                    X[h] = Xr;
            } else {
                if (check_statement(f.run(Xr), F, h)) {
                    if (f.run(Xr)<F[h])
                        X[h] = Xr;
                    double[] Xk = contraction(X[h], Xc, beta);
                    if (f.run(Xk) < F[h]) {
                        X[h] = Xk;
                    } else
                        X = move_points_to_best_point2(X, l, sigma);
                } else
                    X[h] = Xr;
            }
            double tmp2 = f.run(Xc);
//            if(Xc.length == 1)
//                System.out.println("It " + counter + ": f(" + Xc[0] + ") = " + tmp2);
//            else System.out.println("It " + counter + ": f(" + Xc[0] + ", " + Xc[1] + ") = " + tmp2);
            counter++;
            if(counter == 100)
                break;
        } while (stop_criteria(F, Xc) > epsilon);
        if(Xc.length == 1)
            System.out.println("Result: (" + Xc[0] + ")");
        else System.out.println("Result: (" + Xc[0] + ", " + Xc[1] + ")");
    }

    private double[][] generate_points(double[] point, double move, int length) {
        double[][] tmp = new double[length][point.length];
        for (int i = 0; i < point.length; i++)
            tmp[0][i] = point[i];
        for(int i = 0; i < length; i++) {
            if (i != 0)
                point[i - 1] -= move;
            if(point.length != i)
                point[i] += move;
            for (int j = 0; j < point.length; j++)
                if((i+1) != length)
                    tmp[i+1][j] = point[j];
        }
        return tmp;
    }

    private int[] find_largest_smallest(double[] F) {
        double largest = Integer.MIN_VALUE;
        double smallest = Integer.MAX_VALUE;
        int large_index = 0;
        int small_index = 0;
        for (int i = 0; i < F.length; i++) {
            if (F[i] > largest) {
                largest = F[i];
                large_index = i;
            }
        }
        for (int i = 0; i < F.length; i++) {
            if (F[i] < smallest) {
                smallest = F[i];
                small_index = i;
            }
        }

        return new int[] {large_index, small_index};
    }

    private double[] centroid(double[][] X, double h) {
        double[] sum = new double[X[0].length]; //TODO Check if all 0
        for(int i = 0; i < X.length; i++) {
            if (i == h)
                continue;
            for(int j = 0; j < X[0].length; j++) {
                sum[j] += X[i][j];
            }
        }
        for(int i = 0; i < X[0].length; i++)
            sum[i] /= X[0].length;
        return sum;
    }

    private double[] reflexion(double[] Xh, double[] Xc, double alpha) {
        double[] Xr = new double[Xh.length];
        for(int i = 0; i < Xh.length; i++)
            Xr[i] = (1+alpha) * Xc[i] - alpha * Xh[i];
        return Xr;
    }

    private double[] expansion(double[] Xr, double[] Xc, double gamma) {
        double[] Xe = new double[Xr.length];
        for(int i = 0; i < Xr.length; i++)
            Xe[i] = (1-gamma) * Xc[i] + gamma * Xr[i];
        return Xe;
    }

    private double[] contraction(double[] Xh, double[] Xc, double beta) {
        double[] Xk = new double[Xh.length];
        for(int i = 0; i < Xh.length; i++)
            Xk[i] = (1-beta) * Xc[i] + beta * Xh[i];
        return Xk;
    }

    private double stop_criteria(double[] F, double[] Xc) {
        double sum = 0.0;
        int length = F.length;
        for(int i = 0; i < length; i++) {
            sum += Math.pow((F[i] - f.run(Xc)), 2);
        }
        return Math.sqrt(1./length * sum);
    }

    private boolean check_statement(double fXr, double[] F, int h) {
        boolean test = true;
        for(int i = 0; i < F.length; i++) {
            if (i == h)
                continue;
            if(fXr <= F[i]) {
                test = false;
                break;
            }
        }
        return test;
    }

    private double[][] move_points_to_best_point(double[][] X, int l, double sigma) {
        double[][] tmp = new double[X.length][X[0].length];
        for(int i = 0; i < X.length; i++)
            for(int j = 0; j < X[0].length; j++) {
                if(X[l][j] > X[i][j])
                    tmp[i][j] = X[i][j] + sigma;
                else tmp[i][j] = X[i][j] - sigma; //TODO What if equal to best point
            }
        return tmp;
    }

    private double[][] move_points_to_best_point2(double[][] X, int l, double sigma) {
        double[][] tmp = new double[X.length][X[0].length];
        for(int i = 0; i < X.length; i++)
            for(int j = 0; j < X[0].length; j++) {
                tmp[i][j] = X[l][j] + sigma * (X[i][j] - X[l][j]);
            }
        return tmp;
    }

    public double[] hooke_jeeves(double[] X0, double dX, double e) {
        System.out.println("Hooke Jeeves search:");
        double[] Xp, Xb, Xn;
        Xp = copy_values(X0);
        Xb = copy_values(X0);
        int iteration = 0;
        do {
            Xn = find(Xp, dX);
            //print_hooke_jeeves(++iteration, Xb, Xp, Xn);
            if (f.run(Xn)<f.run(Xb)) {
                for (int i = 0; i < Xn.length; i++)
                    Xp[i] = 2 * Xn[i] - Xb[i];
                Xb = copy_values(Xn);
            } else {
                dX /= 2;
                Xp = copy_values(Xb);
            }
        } while (dX > e);
        if(Xb.length == 1)
            System.out.println("Result: (" + Xb[0] + ")");
         else
            System.out.println("Result: (" + Xb[0] + ", " + Xb[1] + ")");

        return Xb;
    }

    private double[] find(double[] Xp, double dX) {
        double[] X = copy_values(Xp);
        for (int i = 0; i < Xp.length; i++) {
            double p = f.run(X);
            X[i] += dX;       // povecamo za Dx
            double n = f.run(X);
            if (n > p) {            // ne valja pozitivni pomak
                X[i] -= 2 * dX;  // smanjimo za Dx
                n = f.run(X);
                if (n > p)          // ne valja ni negativni
                    X[i] += dX;  // vratimo na staro
            }
        }

        return X;
    }


    private void print_hooke_jeeves(int iteration, double[] Xb, double[] Xp, double[] Xn) {
        System.out.println("Iteration " + iteration + "\n-------------");
        if(Xb.length == 1) {
            System.out.println("Xb: (" + Xb[0] + ") - f(Xb) = " + f.run(Xb));
            System.out.println("Xp: (" + Xp[0] + ") - f(Xp) = " + f.run(Xp));
            System.out.println("Xn: (" + Xn[0] + ") - f(Xn) = " + f.run(Xn));
        } else {
            System.out.println("Xb: (" + Xb[0] + ", " + Xb[1] + ") - f(Xb) = " + f.run(Xb));
            System.out.println("Xp: (" + Xp[0] + ", " + Xp[1] + ") - f(Xp) = " + f.run(Xp));
            System.out.println("Xn: (" + Xn[0] + ", " + Xn[1] + ") - f(Xn) = " + f.run(Xn));
        }
    }

    private double[] copy_values(double[] X0) {
        double[] tmp = new double[X0.length];
        for(int i = 0; i < X0.length; i++)
            tmp[i] = X0[i];
        return tmp;
    }

    public double[] axis_search(double[] X0, double[] epsilon) {
        System.out.println("Axis search:");
        double[][] axis_vectors = make_vectors(X0.length);
        double[] Xs;
        int iteration = 0;
        do {
            ++iteration;
            //print_axis_search(X0, iteration);
            Xs = copy_values(X0);
            for (int i = 0; i < axis_vectors.length; i++) {
                double lmin = find_lmin(X0, axis_vectors[i], i); // vraca golden ration / 2
                for (int j = 0; j < X0.length; j++)
                    X0[j] += lmin * axis_vectors[i][j];
            }
            if(iteration == 1000)
                break;
        } while (!axis_search_end(X0, Xs, epsilon));
        if(X0.length == 1)
            System.out.println("Result: (" + X0[0] + ")");
        else System.out.println("Result: (" + X0[0] + ", " + X0[1] + ")");
        //print_axis_search(X0, ++iteration);
        return X0;
    }

    private double[][] make_vectors(int length) {
        double[][] tmp = new double[length][length];
        for(int i = 0; i < length; i++)
            for(int j = 0; j < length; j++) {
                if (i != j) tmp[i][j] = 0.0;
                else tmp[i][j] = 1.0;
            }
        return tmp;
    }

    private void print_axis_search(double[] X0, int iteration) {
        System.out.println("Iteration " + iteration + "\n-------------");
        if (X0.length == 1)
            System.out.println("X: (" + X0[0] + ") - f(X) = " + f.run(X0));
        else System.out.println("X: (" + X0[0] + ", " + X0[1] + ") - f(X) = " + f.run(X0));
    }

    private boolean axis_search_end(double[] X0, double[] Xs, double[] epsilon) {
        boolean flag = true;
        for (int i = 0; i < X0.length; i++) {
            double sum = Math.abs(X0[i] - Xs[i]); //TODO Je li ovo kolko smo se ukupno pomaknuli
            if(sum > epsilon[i]) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private double find_lmin(double[] X0, double[] axis, int optimize_index) {
        double[] interval = unimodal_function(1, 0, X0, axis, optimize_index);
        if (interval == null) {
            if(X0.length == 1)
                System.out.println("Result: (" + X0[0] + ")");
            else System.out.println("Result: (" + X0[0] + ", " + X0[1] + ")");
            exit(1);
        }
        double[] res = golden_ratio_search(optimize_index, X0, interval[0], interval[1], 0.000001);
        double sum = 0;
        for (int i = 0; i < res.length; i++)
            sum += res[i];
        return sum / res.length;
    }

    private double[] unimodal_function(double h, double lambda, double[] X0, double[] axis, int opt_index) {
        double l = lambda - h, r = lambda + h;
        double m = lambda;
        double fl, fm, fr;
        int step = 1;

        double[] tmp = copy_values(X0);
        tmp[opt_index] = X0[opt_index] + lambda;
        fm = f.run(tmp);
        tmp[opt_index] = X0[opt_index] + l;
        fl = f.run(tmp);
        tmp[opt_index] = X0[opt_index] + r;
        fr = f.run(tmp);

        if(fm < fr && fm < fl)
            return null; // TODO Sort if this
        else if(fm > fr)
            do {
                l = m;
                m = r;
                fm = fr;
                r = lambda + h * (step *= 2);
                tmp[opt_index] = X0[opt_index] + r;
                fr = f.run(tmp);
            } while(fm > fr);
        else
            do {
                r = m;
                m = l;
                fm = fl;
                l = lambda - h * (step *= 2);
                tmp[opt_index] = X0[opt_index] + l;
                fl = f.run(tmp);
            } while(fm > fl);


        return new double[] {l, r};
    }
}
