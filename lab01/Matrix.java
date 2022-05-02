import java.io.*;

public class Matrix {
    double[][] _matrix;
    int rows, columns;
    Matrix L;
    Matrix U;
    double[][] pivot;

    public Matrix(String path) {
            createFromFile(path);
    }

    public Matrix(double[][] array) {
        rows = array.length;
        columns = array[0].length;
        _matrix = array;
    }

    public Matrix(double[] array) {
        columns = 1;
        rows = array.length;
        double[][] tempArray = new double[array.length][1];
        for(int i = 0; i < array.length; i++)
            tempArray[i][0] = array[i];
        _matrix = tempArray;
    }

    private int getRows() {
        return rows;
    }

    private int getColumns() {
        return columns;
    }

    private void createFromFile(String path) {
        try {
            FileReader file = new FileReader(path);
            LineNumberReader count = new LineNumberReader(file);

            String[] splitter = count.readLine().split("\\s+");
            columns = splitter.length;
            rows = (int) count.lines().count() + 1;
            count.close();

            FileReader file2 = new FileReader(path);
            BufferedReader br = new BufferedReader(file2);
            String line;
            if ((line = br.readLine()) == null)
                throw new IOException();

            double[][] tempMatrix = new double[rows][columns];
            int i = 0;

            do {
                splitter = line.split("\\s+");
                for(int j = 0; j < columns; j++)
                    tempMatrix[i][j] = Double.parseDouble(splitter[j]);
                i++;
            } while((line = br.readLine()) != null);

            br.close();
            _matrix = tempMatrix;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print() { //TODO Tabulator not working?
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++)
                System.out.print(_matrix[i][j] + "\t");
            System.out.print("\n");
        }
    }

    public void printToFile(String path) {
        try {
            File file = new File(path);
            if(file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println(file.getName() + " changed");
            }
            FileWriter write = new FileWriter(path);
            for(int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    String tmp = _matrix[i][j] + "\t";
                    write.write(tmp);
                }
                write.write("\n");
            }
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Matrix add(Matrix m) {
        // Check appropriate size
        if (rows != m.getRows() || columns != m.getColumns()) {
            System.out.println("Matrices are not the same size!");
            return null;
        }

        double[][] tmp_array = new double[rows][columns];
        double[][] other_matrix = m.getArray();
        for(int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                tmp_array[i][j] = _matrix[i][j] + other_matrix[i][j];

        return new Matrix(tmp_array);
    }

    public Matrix subtract(Matrix m) {
        if (rows != m.getRows() || columns != m.getColumns()) {
            System.out.println("Matrices are not the same size!");
            return null;
        }

        double[][] tmp_array = new double[rows][columns];
        double[][] other_matrix = m.getArray();
        for(int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                tmp_array[i][j] = _matrix[i][j] - other_matrix[i][j];
        return new Matrix(tmp_array);
    }

    private double[][] getArray() {
        return _matrix;
    }

    // Multiply from the right
    public Matrix mul(Matrix m) {
        if(columns != m.getRows()) {
            System.out.println("You cannot multiply these matrices");
            return null;
        }

        double[][] tmp_array = new double[rows][m.getColumns()];
        double[][] other_matrix = m.getArray();
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < m.getColumns(); j++)
                for (int k = 0; k < columns; k++)
                    tmp_array[i][j] += _matrix[i][k] * other_matrix[k][j];
        columns = m.getColumns();
        return new Matrix(tmp_array);
    }

    public Matrix transpose() {
        double[][] transpose = new double[columns][rows];
        for (int i = 0; i < columns; i++)
            for (int j = 0; j < rows; j++)
                transpose[i][j] = _matrix[j][i];
        int tmp = rows;
        rows = columns;
        columns = tmp;
        return new Matrix(transpose);
    }

    public double get_element(int row, int column) {
        return _matrix[row][column];
    }

    public void set_element(int row, int column, double element) {
        _matrix[row][column] = element;
    }

    public Matrix mul_scalar(double scalar) {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                _matrix[i][j] = scalar * _matrix[i][j];
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == Matrix.class) {
            Matrix m = (Matrix) o;
            double[][] other_matrix = m.getArray();
            if(rows != m.getRows() || columns != m.getColumns())
                return false;
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < columns; j++)
                    if(_matrix[i][j] != other_matrix[i][j])
                        return false;
        } else return false;
        return true;
    }

    public Matrix forward_sub(Matrix b, boolean useP) {
        //Vectors b and y are normal vectors - NOT TRANSPOSED - WRITTEN IN ONE COLUMN
        // FOR TESTING PURPOSES
//        rows = 3;
//        double[][] test = {{1, 0, 0}, {1, 1, 0}, {0, 4, 1}};
//        L = new Matrix(test);


        double[][] y = new double[rows][1];
        y[0][0] = b.get_element(0, 0);
        for (int i = 0; i < rows - 1; i++) //TEST THIS
            for (int j = i + 1; j < rows; j ++)
                y[j][0] = b.get_element(j, 0) - (L.get_element(j, i) * y[i][0]);
        return new Matrix(y);
    }

    public Matrix backward_sub(Matrix y) {
        // FOR TESTING PURPOSES
//        rows = 3;
//        double[][] test = {{2, 2, 3}, {0, 1, -3}, {0, 0, 14}};
//        U = new Matrix(test);


        double[][] x = new double[rows][1];
        y.set_element(rows-1, 0, y.get_element(rows-1, 0));
        for (int i = rows-1; i >= 0; i--) {
            double val = 0;
            for (int j = rows-1; j > i; j--)
                val += x[j][0] * U.get_element(i, j);
            val = y.get_element(i, 0) - val;
            x[i][0] = val / U.get_element(i, i);
        }
        return new Matrix(x);
    }

    public Matrix LU_decomposition(Matrix b, boolean useP) { //Test for 0
        double[][] upper_mat = new double[rows][columns];
        double[][] lower_mat = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int k = i; k < rows; k++) {
                int sum = 0;
                for (int j = 0; j < i; j++)
                    sum += (lower_mat[i][j] * upper_mat[j][k]);
                upper_mat[i][k] = _matrix[i][k] - sum;
            }

            for (int k = i; k < rows; k++) {
                if(i == k)
                    lower_mat[i][i] = 1;
                else {
                    int sum = 0;
                    for (int j = 0; j < i; j++)
                        sum += (lower_mat[k][j] * upper_mat[j][i]);
                    lower_mat[k][i] = (_matrix[k][i] - sum) / upper_mat[i][i];
                }
            }
        }
        L = new Matrix(lower_mat);
        U = new Matrix(upper_mat);
        L.print();
        U.print();

        Matrix y = this.forward_sub(b, useP);
        y.print();
        Matrix x = this.backward_sub(y);
        x.print();
        return x;
    }

    public Matrix LUP_decomposition(Matrix b, boolean useP) { //Test for 0
        pivot = new double[rows][columns];
        for(int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++) {
                if(i == j) {
                    pivot[i][j] =1;
                } else pivot[i][j] =0;
            }

        double[][] upper_mat = new double[rows][columns];
        double[][] lower_mat = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            double largest = _matrix[i][i];
            int largest_row_index = 0;
            for(int l = i; l < rows; l++)
                if (Math.abs(_matrix[l][i]) > Math.abs(largest)) { // TODO TEST FOR EPSILON
                    largest = _matrix[l][i];
                    largest_row_index = l;
                }
            switch_rows(i, largest_row_index); // AND MARK IN PIVOT
            for (int k = i; k < rows; k++) {
                int sum = 0;
                for (int j = 0; j < i; j++)
                    sum += (lower_mat[i][j] * upper_mat[j][k]);
                upper_mat[i][k] = _matrix[i][k] - sum;
            }

            for (int k = i; k < rows; k++) {
                if(i == k)
                    lower_mat[i][i] = 1;
                else {
                    int sum = 0;
                    for (int j = 0; j < i; j++)
                        sum += (lower_mat[k][j] * upper_mat[j][i]);
                    lower_mat[k][i] = (_matrix[k][i] - sum) / upper_mat[i][i];
                }
            }
        }
        L = new Matrix(lower_mat);
        U = new Matrix(upper_mat);
//        L.print();
//        U.print();

        Matrix y = this.forward_sub(b, useP);
//        y.print();
        Matrix x = this.backward_sub(y);
//        x.print();
        return x;
    }

    private void switch_rows(int source, int target) {
        double[] tmp = new double[columns];
        double[] pivot_tmp = new double[columns];
        for(int i = 0; i < columns; i++) {
            tmp[i] = _matrix[target][i]; // COPIED TARGET
            pivot_tmp[i] = pivot[target][i];
        }
        for(int i = 0; i < columns; i++) {
            _matrix[target][i] = _matrix[source][i]; // REPLACED TARGET WITH SOURCE
            pivot[target][i] = pivot[source][i];
        }
        for(int i = 0; i < columns; i++) {
            _matrix[source][i] = tmp[i]; // COPIED TARGET TO SOURCE
            pivot[source][i] = pivot_tmp[i];
        }
    }
}
