import java.io.IOException;

public class Tests {
    public static void main(String[] args) {
        double[][] m = {{6, 2, 10}, {2, 3, 0}, {0, 4, 2}};
        double[] v = {2, 3, 4};
        String path = "C:\\Users\\danie\\Desktop\\test.txt";
        String path2 = "C:\\Users\\danie\\Desktop\\test2.txt";

        Matrix A = new Matrix(path);
        Matrix B = new Matrix(m);
        Matrix C = new Matrix(v);
        double[] b_array = {25, 10, 10};
        Matrix b = new Matrix(b_array);
//        Matrix y = A.forward_sub(b, false);
//        y.print();
//        Matrix x = A.backward_sub(y);
//        x.print();
        B.LU_decomposition(C, false);
//        double[][] test_matrix = {{4,10,5,-9}, {-4,6,6,3}, {-8,4,2,6}, {-4,8,1,-3}};
//        Matrix test_matri = new Matrix(test_matrix);
//        test_matri.LUP_decomposition(b, false);

//        A = A.add(B);
//        A = A.subtract(B);
//        A = A.mul(B);
//        A = A.mul_scalar(3);
//        A = A.transpose();
//        A.set_element(0,0,5);
//        double a = A.get_element(0,1);
//        boolean bool = A.equals(B);
//        A.print();
//        A.printToFile(path2);
//        C.print();
//        C = C.transpose();
//        Matrix res = C.mul(A);
//        res.print();
    }


}