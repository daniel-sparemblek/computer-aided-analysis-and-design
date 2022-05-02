import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GA {
    private final boolean use_binary; // else use_double
    private final int pop_size;
    private final double mutation_prob;
    private final boolean arithmetic_crx; //else use heuristic
    private final boolean single_point_crossover; // else use uniform
    private final int goal_fun_evaluation;
    private final double upper_limit;
    private final double lower_limit;
    private final int dimension;
    private final int decimal_precision;
    private final int k_tournament = 3;
    private final boolean run_verbose;
    private final double end_precision;
    private int num_of_bits;

    private final Functions f;


    public GA(boolean use_binary, double upper_limit, double lower_limit,
              int decimal_precision, int pop_size, int dimension,
              Functions f, double mutation_prob, int goal_fun_evaluation,
              boolean arithmetic_crx, boolean single_point_crossover,
              boolean run_verbose, double end_precision) {
        this.use_binary = use_binary;
        this.upper_limit = upper_limit;
        this.lower_limit = lower_limit;
        this.decimal_precision = decimal_precision;
        this.pop_size = pop_size;
        this.dimension = dimension;
        this.f = f;
        this.mutation_prob = mutation_prob;
        this.goal_fun_evaluation = goal_fun_evaluation;
        this.arithmetic_crx = arithmetic_crx;
        this.single_point_crossover = single_point_crossover;
        this.run_verbose = run_verbose;
        this.end_precision = end_precision;
    }

    public void run() {
        if(use_binary)
            _run_binary();
        else
            _run_double();
    }

    private void _run_binary() {
        // Create initial population
        List<List<List<Integer>>> population = create_population();
        List<List<Double>> real_numbers = transform_binary_to_real(population);

        // Calculate goal function
        List<Double> goal_eval = new LinkedList<>();
        for(List<Double> point : real_numbers)
            goal_eval.add(eval_goal(point));

        // Calculate fitness function
        List<Double> fitness_eval = eval_fitness(goal_eval);

        do {
            // Select 3 random values from population
            final int[] random_ints = new Random().ints(0, population.size()).distinct().limit(3).toArray();

            // Delete the worst of the 3 from the population
            int worst = random_ints[0];
            int best = random_ints[0];
            for(int i : random_ints) {
                if (fitness_eval.get(i) < fitness_eval.get(worst))
                    worst = i;
                if (fitness_eval.get(i) > fitness_eval.get(worst))
                    best = i;
            }

            // Get new value from crossing the remaining two values.
            // There is no cross probability because an elimination
            // selection process was used instead of a generational one.
            List<List<Integer>> new_value = cross_binary(worst, random_ints, population);

            // Mutate new value
            new_value = binary_mutate(new_value);

            // Add new value to population replacing the old one
            population.set(worst, new_value);

            // Evaluate new value
            real_numbers = transform_binary_to_real(population);
            goal_eval = new LinkedList<>();
            for(List<Double> point : real_numbers)
                goal_eval.add(eval_goal(point));
            fitness_eval = eval_fitness(goal_eval);

            if(run_verbose) {
                System.out.println("Evaluation " + f.getNum_of_calls() + ": ");
                System.out.println("\tCurrent best chromosome: " + print_chromosome_binary(population.get(best)));
                System.out.println("\tCurrent best chromosome(real): " + print_chromosome(real_numbers.get(best)));
                System.out.println("\tGoal function for best chromosome: " + goal_eval.get(best));
            }
            if(goal_eval.get(best) < end_precision) {
                System.out.println("Evaluation " + f.getNum_of_calls() + ": ");
                System.out.println("\tCurrent best chromosome: " + print_chromosome_binary(population.get(best)));
                System.out.println("\tCurrent best chromosome(real): " + print_chromosome(real_numbers.get(best)));
                System.out.println("\tGoal function for best chromosome: " + goal_eval.get(best));
                break;
            }
        } while(f.getNum_of_calls() < goal_fun_evaluation);
    }

    private String print_chromosome_binary(List<List<Integer>> lists) {
        StringBuilder values = new StringBuilder("(");
        for(List<Integer> li : lists) {
            for(int i = num_of_bits -1; i >= 0; i--)
                values.append(li.get(i));
            values.append(", ");
        }
        values.delete(values.length()-2, values.length());
        values.append(")");
        return values.toString();
    }

    private List<List<Integer>> binary_mutate(List<List<Integer>> new_value) {
        Random rand = new Random();
        double pm = 1 - Math.pow((1 - mutation_prob), num_of_bits);
        if(Math.random() < pm) {
            List<List<Integer>> mutated = new LinkedList<>();
            for(List<Integer> chromosome : new_value) {
                int mutate_bit_pos = rand.nextInt(num_of_bits - 1 + 1);
                int mutate_bit = chromosome.get(mutate_bit_pos);
                mutate_bit ^= 1;
                chromosome.set(mutate_bit_pos, mutate_bit);
                mutated.add(chromosome);
            }
            return mutated;

        } else return new_value;
    }

    private List<List<Integer>> cross_binary(int worst, int[] random_ints,
                                            List<List<List<Integer>>> population) {
        int[] selection = new int[k_tournament - 1];
        int j = 0;
        for (int random_int : random_ints)
            if (random_int != worst) {
                selection[j] = random_int;
                j++;
            }
        List<List<Integer>> x1 = population.get(selection[0]);
        List<List<Integer>> x2 = population.get(selection[1]);

        List<List<Integer>> cross_product = new LinkedList<>();
        if(single_point_crossover) {
            Random single_point_crx = new Random();
            // Split binary number AFTER random_split-th position
            int random_split = single_point_crx.nextInt(num_of_bits - 2 + 1);

            for (int i = 0; i < dimension; i++) {
                List<Integer> child = create_child(x1.get(i), x2.get(i), random_split);
                cross_product.add(child);
            }
        } else {
            for(int i = 0; i < dimension; i++) {
                // Generate random chromosome
                List<Integer> R = new LinkedList<>();
                for (int k = 0; k < num_of_bits; k++) {
                    R.add((int) (Math.random() * 2));
                }

                List<Integer> child = create_uniform_child(x1.get(i), x2.get(i), R);
                cross_product.add(child);
            }
        }
        return cross_product;
    }

    private List<Integer> create_uniform_child(List<Integer> x1, List<Integer> x2, List<Integer> R) {
        List<Integer> child = new LinkedList<>();
        for(int i = 0; i < num_of_bits; i++) {
            child.add((x1.get(i) & x2.get(i)) + R.get(i) * (x1.get(i) ^ x2.get(i)));
        }
        return child;
    }

    private List<Integer> create_child(List<Integer> x1, List<Integer> x2, int split_point) {
        
        List<Integer> child = new LinkedList<>();
        for(int i = 0; i < num_of_bits; i++) {
            if (i > split_point)
                child.add(x2.get(i));
            else
                child.add(x1.get(i));
        }
        return child;
    }

    private List<List<Double>> transform_binary_to_real(List<List<List<Integer>>> population) {
        // Convert bits to binary numbers
        List<List<Integer>> population_binary = new LinkedList<>();

        for(List<List<Integer>> binary_points : population) {
            List<Integer> points_real = new LinkedList<>();
            for (List<Integer> chromosome : binary_points) {
                int sum = 0;
                for (int i = 0; i < num_of_bits; i++) {
                    sum += chromosome.get(i) * Math.pow(2, i);
                }
                points_real.add(sum);
            }
            population_binary.add(points_real);
        }

        // Transform from b to x using formula from pdf
        List<List<Double>> population_real = new LinkedList<>();

        for (List<Integer> pb : population_binary) {
            List<Double> vector_real = new LinkedList<>();
            for(int b : pb) {
                double x = lower_limit + (b * 1. / Math.pow(2, num_of_bits)) * (upper_limit - lower_limit);
//                vector_real.add(round(x, decimal_precision));
                vector_real.add(x);
            }
            population_real.add(vector_real);
        }
        return population_real;
    }

    private List<List<List<Integer>>> create_population() {
        List<List<List<Integer>>> population = new LinkedList<>();


        double range = upper_limit - lower_limit;
        double bits = Math.pow(10, decimal_precision) * range;
        num_of_bits = calc_bit_number(bits);

        for(int i = 0; i < pop_size; i++) {
            List<List<Integer>> binary_point = new LinkedList<>();
            for (int j = 0; j < dimension; j++) {
                List<Integer> chromosome = new LinkedList<>();
                for (int k = 0; k < num_of_bits; k++) {
                    chromosome.add((int) (Math.random() * 2));
                }
                binary_point.add(chromosome);
            }
            population.add(binary_point);
        }
        return population;
    }

    private int calc_bit_number(double bits) {
        if (bits <= 2)
            return 1;
        else if (bits <= 4)
            return 2;
        else if (bits <= 8)
            return 3;
        else if (bits <= 16)
            return 4;
        else if (bits <= 32)
            return 5;
        else if (bits <= 64)
            return 6;
        else if (bits <= 128)
            return 7;
        else if (bits <= 256)
            return 8;
        else if (bits <= 512)
            return 9;
        else if (bits <= 1024)
            return 10;
        else if (bits <= 2048)
            return 11;
        else if (bits <= 4096)
            return 12;
        else if (bits <= 8192)
            return 13;
        else if (bits <= 16384)
            return 14;
        else if (bits <= 32768)
            return 15;
        else if (bits <= 65536)
            return 16;
        else if (bits <= 131072)
            return 17;
        else if (bits <= 262144)
            return 18;
        else if (bits <= 524288)
            return 19;
        else if (bits <= 1048576)
            return 20;
        else if (bits <= 2097152)
            return 21;
        else if (bits <= 4194304)
            return 22;
        else {
            System.err.println("Range too large");
            System.exit(1);
            return 0;
        }
    }

    private void _run_double() {
        // Create initial population
        List<List<Double>> population = create_population_double();

        // Calculate goal function
        List<Double> goal_eval = new LinkedList<>();
        for(List<Double> point : population)
            goal_eval.add(eval_goal(point));

        // Calculate fitness function
        List<Double> fitness_eval = eval_fitness(goal_eval);


        // Elitism is automatically used in tournament selection
        do {
            // Select 3 random values from population
            final int[] random_ints = new Random().ints(0, population.size()).distinct().limit(3).toArray();

            // Delete the worst of the 3 from the population
            int worst = random_ints[0];
            int best = random_ints[0];
            for(int i : random_ints) {
                if (fitness_eval.get(i) < fitness_eval.get(worst))
                    worst = i;
                if (fitness_eval.get(i) > fitness_eval.get(worst))
                    best = i;
            }

            // Get new value from crossing the remaining two values.
            // There is no cross probability because an elimination
            // selection process was used instead of a generational one.
            List<Double> new_value = cross(worst, best, random_ints, population);

            // Mutate new value
            new_value = mutate(new_value);

            // Evaluate new value
            double new_goal_eval = eval_goal(new_value);
            goal_eval.set(worst, new_goal_eval);
            fitness_eval = eval_fitness(goal_eval);

            // Add new value to population replacing the old one
            population.set(worst, new_value);

            if(run_verbose) {
                System.out.println("Evaluation " + f.getNum_of_calls() + ": ");
                System.out.println("\tCurrent best chromosome: " + print_chromosome(population.get(best)));
                System.out.println("\tGoal function for best chromosome: " + goal_eval.get(best));
            }
            if(Math.abs(goal_eval.get(best)) < end_precision) {
                System.out.println("Evaluation " + f.getNum_of_calls() + ": ");
                System.out.println("\tCurrent best chromosome: " + print_chromosome(population.get(best)));
                System.out.println("\tGoal function for best chromosome: " + goal_eval.get(best));
                break;
            }
        } while(f.getNum_of_calls() < goal_fun_evaluation);
    }

    private String print_chromosome(List<Double> doubles) {
        StringBuilder values = new StringBuilder("(");
        for(double d : doubles)
            values.append(d).append(", ");
        values.delete(values.length()-2, values.length());
        values.append(")");
        return values.toString();
    }

    private List<Double> mutate(List<Double> new_value) {
        if(Math.random() < mutation_prob) {
            Random r = new Random();
            List<Double> mutation = new LinkedList<>();
            for(int i = 0; i < dimension; i++)
                mutation.add(lower_limit + (upper_limit - lower_limit) * r.nextDouble());
            return mutation;
        } else return new_value;
    }

    private List<Double> cross(int worst, int best, int[] random_ints, List<List<Double>> population) {
        //When using arithmetical crossover, we are using a WHOLE arithmetic crossover
        double a = Math.random();
        int[] selection = new int[k_tournament - 1];
        int j = 0;
        for (int random_int : random_ints)
            if (random_int != worst) {
                selection[j] = random_int;
                j++;
            }
        List<Double> x1 = population.get(selection[0]);
        List<Double> x2 = population.get(selection[1]);
        if(!arithmetic_crx)
            if (selection[0] == best) {
                x1 = population.get(selection[1]);
                x2 = population.get(selection[0]);
            }

        List<Double> new_value = new LinkedList<>();
        if(arithmetic_crx)
            for(int i = 0; i < dimension; i++)
                new_value.add(a * x1.get(i) + (1-a) * x2.get(i));
        else {
            for (int i = 0; i < dimension; i++)
                new_value.add(a * (x2.get(i) - x1.get(i)) + x2.get(i));
        }

        return new_value;
    }

    private List<List<Double>> create_population_double() {
        List<List<Double>> population = new LinkedList<>();
        for(int i = 0; i < pop_size; i++) {
            List<Double> point = new LinkedList<>();
            for(int j = 0; j < dimension; j++) {
                Random r = new Random();
                double random_value = lower_limit + (upper_limit - lower_limit) * r.nextDouble();
                point.add(random_value); // No rounding in double_mode
            }
            population.add(point);
        }
        return population;
    }

    private List<Double> eval_fitness(List<Double> goal_eval) {
        int best_index = 0;
        int worst_index = 0;
        int i = 0;
        for(double goal : goal_eval) {
            if(goal < goal_eval.get(best_index))
                best_index = i;
            if(goal > goal_eval.get(worst_index))
                worst_index = i;
            i++;
        }

        List<Double> fitness_eval = new LinkedList<>();
        for (double fi : goal_eval)
            fitness_eval.add(1+((fi - goal_eval.get(worst_index)) /
                    (goal_eval.get(best_index) - goal_eval.get(worst_index))));
        return fitness_eval;
    }

    private double eval_goal(List<Double> points) {
        double[] x = new double[points.size()];
        for(int i = 0; i < points.size(); i++)
            x[i] = points.get(i);
        return f.run(x);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
