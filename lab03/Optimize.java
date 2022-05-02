public class Optimize {

    private Functions f;
    private final int num_of_dim = 2;

    public Optimize(Functions f) {
        this.f = f;
    }

    public void gradient_descent(double[] start_point, double precision, boolean use_golden_ratio) {
        double[] point = start_point;
        double[] gradient;
        double norm;
        do {
            gradient = f.get_gradient(point);
            norm = calc_norm(gradient);
            if(use_golden_ratio) {
                f.define(point, gradient);

            } else {
                point[0] += gradient[0];
                point[1] += gradient[1];
            }
//            System.out.println(norm);#
            if(f.getNum_of_calls() > 2000) break;
        } while (norm > precision);
        System.out.println(point[0] + ", " + point[1]);
    }

    private double calc_norm(double[] vector) {
        return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
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

    public void box(double[] start_point, double alpha, double precision) {
        if(!f.check_explicit(start_point) || !f.check_implicit(start_point))
            throw new NullPointerException();
        double[] centroid = start_point;
        double[][] points = new double[2*num_of_dim][num_of_dim];
        double[] F = new double[2*num_of_dim];
        points[0] = start_point;
        for (int i = 1; i < 2*num_of_dim; i++) {
            double new_point[] = new double[num_of_dim];
            for(int j = 0; j < num_of_dim; j++) {
                new_point[j] = -100 + Math.random() * (100 + 100);
            }
            while (!f.check_implicit(new_point)) {
                for(int j = 0; j < num_of_dim; j++) {
                    new_point[j] = 1/2. * (new_point[j] + centroid[j]);
                }
            }
            points[i] = new_point;
            centroid = calc_centroid(points, -1);
        }
        double old_centroid = 0.;
        do {
            int h = 0;
            int h2 = 0;

            for (int i = 0; i < points.length; i++) {
                F[i] = f.run(points[i]);
            }
            double f_max = F[0];
            double f_max2 = F[0];
            for(int i = 1; i < F.length; i++) {
                if(F[i] > f_max) {
                    f_max = F[i];
                    h = i;
                }
                if(F[i] > f_max2) {
                    f_max2 = F[i];
                    h2 = i;
                }
            }
            centroid = calc_centroid(points, h);
            double[] Xr = new double[num_of_dim];
            Xr[0] = (1+alpha)*centroid[0] - alpha*points[h][0];
            Xr[1] = (1+alpha)*centroid[1] - alpha*points[h][1];
            for (int i = 0; i < num_of_dim; i++) {
                if(Xr[i] < -100)
                    Xr[i] = -100;
                else if(Xr[i] > 100)
                    Xr[i] = 100;
            }
            while (!f.check_implicit(Xr))
                for(int i = 0; i < num_of_dim; i++)
                    Xr[i] = 1/2. * (Xr[i] + centroid[i]);
            if(f.run(Xr) > F[h2])
                for(int i = 0; i < num_of_dim; i++)
                    Xr[i] = 1/2. * (Xr[i] + centroid[i]);
            points[h] = Xr;
            if(old_centroid == centroid[0])
                 break;
            old_centroid = centroid[0];
        } while(stop_criteria(F, centroid) > precision);
        System.out.println(centroid[0] + " " + centroid[1]);
    }

    private double[] calc_centroid(double[][] points, int skip) {
        double[] new_centroid = new double[num_of_dim];
        new_centroid[0] = 0.0;
        new_centroid[1] = 0.0;
        for (int i = 0; i < points.length; i++) {
            if(i == skip) continue;
            for(int j = 0; j < num_of_dim; j++) {
                new_centroid[j] += points[i][j];
            }
        }
        new_centroid[0] /= points.length;
        new_centroid[1] /= points.length;
        return  new_centroid;
    }

    private double stop_criteria(double[] F, double[] centroid) {
        double sum = 0.0;
        int length = F.length;
        for(int i = 0; i < length; i++) {
            sum += Math.pow((F[i] - f.run(centroid)), 2);
        }
        return Math.sqrt(1./length * sum);
    }

    public void transform_mixed(double[] start_point, int t, double precision) {
        double[] res = new double[num_of_dim];
        double[] old_res = start_point;
        boolean outside_limits = false;
        if(!f.check_implicit(start_point)) {
            System.out.println("Start point (" + start_point[0] + ", " + start_point[1] + ") is outside of the boundaries");
            outside_limits = true;
        }
        if(outside_limits)
            start_point = hooke_jeeves(start_point, 0.5, precision, t, true);
        System.out.println("New start point is: " + start_point[0] + ", " + start_point[1]);
        do {
            old_res = res;
            res = hooke_jeeves(start_point, 0.5, precision, t, false);
            t *= 10;
        } while(stop_criteria2(old_res, res, precision));
        System.out.println(res[0] + ", " + res[1]);
    }

    private boolean stop_criteria2(double[] old_res, double[] res, double precision) {
        if(Math.abs(old_res[0] - res[0]) < precision && Math.abs(old_res[1] - res[1]) < precision)
            return false;
        return true;
    }

    public double[] hooke_jeeves(double[] X0, double dX, double e, int t, boolean outside) {

        double[] Xp, Xb, Xn;
        Xp = copy_values(X0);
        Xb = copy_values(X0);
        int iteration = 0;
        do {
            Xn = find(Xp, dX, t, outside);
            //print_hooke_jeeves(++iteration, Xb, Xp, Xn);
            boolean tmp;
            if(outside)
                tmp = (f.find_new_point(Xn)<f.find_new_point(Xb));
            else tmp = f.function_U(Xn, t)<f.function_U(Xb, t);
            if (tmp) {
                for (int i = 0; i < Xn.length; i++)
                    Xp[i] = 2 * Xn[i] - Xb[i];
                Xb = copy_values(Xn);
            } else {
                dX /= 2;
                Xp = copy_values(Xb);
            }
        } while (dX > e);
//        if(Xb.length == 1)
//            System.out.println("Result: (" + Xb[0] + ")");
//        else
//            System.out.println("Result: (" + Xb[0] + ", " + Xb[1] + ")");
        return Xb;
    }

    private double[] find(double[] Xp, double dX, int t, boolean out) {
        double[] X = copy_values(Xp);
        for (int i = 0; i < Xp.length; i++) {
            double p;
            if(out)
                p = f.find_new_point(X);
            else p = f.function_U(X, t);
            X[i] += dX;       // povecamo za Dx
            double n;
            if(out)
                n = f.find_new_point(X);
            else n = f.function_U(X, t);
            if (n > p) {            // ne valja pozitivni pomak
                X[i] -= 2 * dX;  // smanjimo za Dx
                if(out)
                    n = f.find_new_point(X);
                else n = f.function_U(X, t);
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

    public void newton_raphson(double[] start_point, double precision, boolean use_golden_ratio) {
        double[][] hesse = f.get_hesse(start_point);
        System.out.println("Hesse Matrix");
        System.out.println("\t" + hesse[0][0] + ", " + hesse[0][1]);
        System.out.println("\t" + hesse[1][0] + ", " + hesse[1][1]);
    }
}
