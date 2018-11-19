package deeplearning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bp {

	private double[] hide1_x;//// 输入层即第一层隐含层的输入；hide1_x[数据的特征数目+1]， hide1_x[0]为1
	private double[][] hide1_w;// 隐含层权值，hide1_w[本层的节点的数目][数据的特征数目+1];hide_w[0][0]为偏置量
	private double[] hide1_errors;// 隐含层的误差,hide1_errors[节点个数]

	private double[] out_x;// 输出层的输入值即第二次层隐含层的输出 out_x[上一层的节点数目+1]， out_x[0]为1
	private double[][] out_w;// 输出层的权值 hide1_w[节点的数目][上一层的节点数目+1]//
								// out_w[0][0]为偏置量
	private double[] out_errors;// 输出层的误差 hide1_errors[节点个数]

	private double[] target;// 目标值，target[输出层的节点个数]

	private double rate;// 学习速率

	public Bp(int input_node, int hide1_node, int out_node, double rate) {
		super();

		// 输入层即第一层隐含层的输入
		hide1_x = new double[input_node + 1];

		// 第一层隐含层
		hide1_w = new double[hide1_node][input_node + 1];
		hide1_errors = new double[hide1_node];

		// 输出层
		out_x = new double[hide1_node + 1];
		out_w = new double[out_node][hide1_node + 1];
		out_errors = new double[out_node];

		target = new double[out_node];

		// 学习速率
		this.rate = rate;
		init_weight();// 1.初始化网络的权值
	}

	/**
	 * 初始化权值
	 */
	public void init_weight() {

		set_weight(hide1_w);
		set_weight(out_w);
	}

	/**
	 * 初始化权值
	 * 
	 * @param w
	 */
	private void set_weight(double[][] w) {
		for (int i = 0, len = w.length; i != len; i++)
			for (int j = 0, len2 = w[i].length; j != len2; j++) {
				w[i][j] = 0;
			}
	}

	/**
	 * 获取原始数据
	 * 
	 * @param Data 原始数据矩阵
	 */
	private void setHide1_x(double[] Data) {
		if (Data.length != hide1_x.length - 1) {
			throw new IllegalArgumentException("数据大小与输出层节点不匹配");
		}
		System.arraycopy(Data, 0, hide1_x, 1, Data.length);
		hide1_x[0] = 1.0;
	}

	/**
	 * @param target the target to set
	 */
	private void setTarget(double[] target) {
		this.target = target;
	}

	/**
	 * 2.训练数据集
	 * 
	 * @param TrainData 训练数据
	 * @param target    目标
	 */
	public void train(double[] TrainData, double[] target) {
		// 2.1导入训练数据集和目标值
		setHide1_x(TrainData);
		setTarget(target);

		// 2.2：向前传播得到输出值；
		double[] output = new double[out_w.length + 1];
		forword(hide1_x, output);

		// 2.3、方向传播：
		backpropagation(output);

	}

	/**
	 * 反向传播过程
	 * 
	 * @param output 预测结果
	 */
	public void backpropagation(double[] output) {

		// 2.3.1、获取输出层的误差；
		get_out_error(output, target, out_errors);
		// 2.3.2、获取隐含层的误差；
		get_hide_error(out_errors, out_w, out_x, hide1_errors);
		//// 2.3.3、更新隐含层的权值；
		update_weight(hide1_errors, hide1_w, hide1_x);
		// * 2.3.4、更新输出层的权值；
		update_weight(out_errors, out_w, out_x);
	}

	/**
	 * 预测
	 * 
	 * @param data   预测数据
	 * @param output 输出值
	 */
	public void predict(double[] data, double[] output) {

		double[] out_y = new double[out_w.length + 1];
		setHide1_x(data);
		forword(hide1_x, out_y);
		System.arraycopy(out_y, 1, output, 0, output.length);

	}

	public void update_weight(double[] err, double[][] w, double[] x) {

		double newweight = 0.0;
		for (int i = 0; i < w.length; i++) {
			for (int j = 0; j < w[i].length; j++) {
				newweight = rate * err[i] * x[j];
				w[i][j] = w[i][j] + newweight;
			}

		}
	}

	/**
	 * 获取输出层的误差
	 * 
	 * @param output    预测输出值
	 * @param target    目标值
	 * @param out_error 输出层的误差
	 */
	public void get_out_error(double[] output, double[] target, double[] out_error) {
		for (int i = 0; i < target.length; i++) {
			out_error[i] = (target[i] - output[i + 1]) * output[i + 1] * (1d - output[i + 1]);
		}

	}

	/**
	 * 获取隐含层的误差
	 * 
	 * @param NeLaErr 下一层的误差
	 * @param Nextw   下一层的权值
	 * @param output  下一层的输入
	 * @param error   本层误差数组
	 */
	public void get_hide_error(double[] NeLaErr, double[][] Nextw, double[] output, double[] error) {

		for (int k = 0; k < error.length; k++) {
			double sum = 0;
			for (int j = 0; j < Nextw.length; j++) {
				sum += Nextw[j][k + 1] * NeLaErr[j];
			}
			error[k] = sum * output[k + 1] * (1d - output[k + 1]);
		}
	}

	/**
	 * 向前传播
	 * 
	 * @param x      输入值
	 * @param output 输出值
	 */
	public void forword(double[] x, double[] output) {

		// 2.2.1、获取隐含层的输出
		get_net_out(x, hide1_w, out_x);
		// 2.2.2、获取输出层的输出
		get_net_out(out_x, out_w, output);

	}

	/**
	 * 获取单个节点的输出
	 * 
	 * @param x 输入矩阵
	 * @param w 权值
	 * @return 输出值
	 */
	private double get_node_put(double[] x, double[] w) {
		double z = 0d;

		for (int i = 0; i < x.length; i++) {
			z += x[i] * w[i];
		}
		// 2.激励函数
		return 1d / (1d + Math.exp(-z));
	}

	/**
	 * 获取网络层的输出
	 * 
	 * @param x       输入矩阵
	 * @param w       权值矩阵
	 * @param net_out 接收网络层的输出数组
	 */
	private void get_net_out(double[] x, double[][] w, double[] net_out) {

		net_out[0] = 1d;
		for (int i = 0; i < w.length; i++) {
			net_out[i + 1] = get_node_put(x, w[i]);
		}

	}

    public static void main(String[] args) throws IOException {
        
    
        Bp bp = new Bp(32, 15, 4, 0.05);

        Random random = new Random();
        
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i != 6000; i++) {
            int value = random.nextInt();
            list.add(value);
        }

        for (int i = 0; i !=25; i++) {
            for (int value : list) {
                double[] real = new double[4];
                if (value >= 0)
                    if ((value & 1) == 1)
                        real[0] = 1;
                    else
                        real[1] = 1;
                else if ((value & 1) == 1)
                    real[2] = 1;
                else
                    real[3] = 1;
                
                double[] binary = new double[32];
                int index = 31;
                do {
                    binary[index--] = (value & 1);
                    value >>>= 1;
                } while (value != 0);

               System.out.println(binary+" "+value);
            }
        }
        
        

        
        
        System.out.println("训练完毕，下面请输入一个任意数字，神经网络将自动判断它是正数还是复数，奇数还是偶数。");

        while (true) {
            
            byte[] input = new byte[10];
            System.in.read(input);
            Integer value = Integer.parseInt(new String(input).trim());
            int rawVal = value;
            double[] binary = new double[32];
            int index = 31;
            do {
                binary[index--] = (value & 1);
                value >>>= 1;
            } while (value != 0);

            double[] result =new double[4];
             bp.predict(binary,result);

             
            double max = -Integer.MIN_VALUE;
            int idx = -1;

            for (int i = 0; i != result.length; i++) {
                if (result[i] > max) {
                    max = result[i];
                    idx = i;
                }
            }

            switch (idx) {
            case 0:
                System.out.format("%d是一个正奇数\n", rawVal);
                break;
            case 1:
                System.out.format("%d是一个正偶数\n", rawVal);
                break;
            case 2:
                System.out.format("%d是一个负奇数\n", rawVal);
                break;
            case 3:
                System.out.format("%d是一个负偶数\n", rawVal);
                break;
            }
        }
    }

}
