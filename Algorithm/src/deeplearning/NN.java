package deeplearning;
import java.util.Random;
/**
 * 
 * @author bob
 *
 */
public class NN {

	double[][] weightInput2Hidden;
	double[][] weightHidden2Output;
	double[] hiddenBias;
	double[] outputBias;
	int inputNodeNum;
	int hiddenNodeNum;
	int outputNodeNum;
	Random random = new Random();
	public double sigmoid(double value) {
		return StrictMath.tanh(value);//(-1,1)
//		return (Math.pow(Math.E, value)-Math.pow(Math.E, -value))/(Math.pow(Math.E, value)+Math.pow(Math.E, -value));
	}
	public double sigmoidDiv(double value) {
		return 1-sigmoid(value)*sigmoid(value);
	}
	public void randomSet(int inputNodeNum, int hiddenNodeNum, int outputNodeNum) {
		this.inputNodeNum=inputNodeNum;
		this.hiddenNodeNum=hiddenNodeNum;
		this.outputNodeNum=outputNodeNum;
		
		weightInput2Hidden= new double[inputNodeNum][];
		weightHidden2Output= new double[hiddenNodeNum][];
		hiddenBias=new double[hiddenNodeNum];
		outputBias=new double[outputNodeNum];
		for(int i=0;i<inputNodeNum;i++) {
			weightInput2Hidden[i]=new double[hiddenNodeNum];
			for(int k=0;k<hiddenNodeNum;k++) {
				weightInput2Hidden[i][k]=(random.nextDouble()*2-1)/2;//[-0.5,0.5)
			}
		}
		for(int i=0;i<hiddenNodeNum;i++) {
			weightHidden2Output[i]=new double[outputNodeNum];
			for(int k=0;k<outputNodeNum;k++) {
				weightHidden2Output[i][k]=(random.nextDouble()*2-1)/2;//[-0.5,0.5)
			}
		}
		for(int i=0;i<hiddenNodeNum;i++) {
			hiddenBias[i]=(random.nextDouble()*2-1)/2;//[-0.5,0.5)
		}
		for(int i=0;i<outputNodeNum;i++) {
			outputBias[i]=(random.nextDouble()*2-1)/2;//[-0.5,0.5)
		}
	}
	public NN(double[][] inputs,int inputNodeNum, int hiddenNodeNum, int outputNodeNum, double[][] outputs,double lRate) {
		randomSet(inputNodeNum,  hiddenNodeNum,  outputNodeNum);
		int epochs=100000;
		for(int ii=0;ii<epochs;ii++) {
			//-->>
			// get the value of hidden node
			int index=random.nextInt(inputs.length);
			double[] input=inputs[index];
			double[] output=outputs[index];
			double[] hiddenY=new double[hiddenNodeNum];
			for(int j=0;j<hiddenNodeNum;j++) {
				hiddenY[j]=0;
				for(int k=0;k<inputNodeNum;k++) {
					hiddenY[j]+=input[k]*weightInput2Hidden[k][j];
				}
				hiddenY[j]+=hiddenBias[j];
				hiddenY[j]=sigmoid(hiddenY[j]);
			}
			// get the value of output node
			double[] outputY=new double[outputNodeNum];
			for(int j=0;j<outputNodeNum;j++) {
				outputY[j]=0;
				for(int k=0;k<hiddenNodeNum;k++) {
					outputY[j]+=hiddenY[k]*weightHidden2Output[k][j];
				}
				outputY[j]+=outputBias[j];
				outputY[j]=sigmoid(outputY[j]);
			}
			//<<---update the weight and bias
			double[] outputErr=new double[outputNodeNum];
			for(int j=0;j<outputNodeNum;j++) {
				outputErr[j]=(1-outputY[j]*outputY[j])*(output[j]-outputY[j]);
			}
			double[] errHidden=new double[hiddenNodeNum];
			for(int j=0;j<hiddenNodeNum;j++) {
				errHidden[j]=0;
				for(int k=0;k<outputNodeNum;k++) {
					errHidden[j]+=outputErr[k]*weightHidden2Output[j][k];
				}
				errHidden[j]*=1-hiddenY[j]*hiddenY[j];
			}
			for(int j=0;j<hiddenNodeNum;j++) {
				for(int k=0;k<outputNodeNum;k++) {
					weightHidden2Output[j][k]+=lRate*outputErr[k]*hiddenY[j];
				}
			}
			for(int j=0;j<inputNodeNum;j++) {
				for(int k=0;k<hiddenNodeNum;k++) {
					weightInput2Hidden[j][k]+=lRate*errHidden[k]*input[j];
				}
			}
			for(int j=0;j<outputNodeNum;j++) {
				outputBias[j]+=lRate*outputErr[j];
			}
			for(int j=0;j<hiddenNodeNum;j++) {
				hiddenBias[j]+=lRate*errHidden[j];
			}
//			System.out.println();
		}//for
	}
	
	public double[] predict(double[] input) {
		double[] hiddenY=new double[hiddenNodeNum];
		for(int j=0;j<hiddenNodeNum;j++) {
			hiddenY[j]=0;
			for(int k=0;k<inputNodeNum;k++) {
				hiddenY[j]+=input[k]*weightInput2Hidden[k][j];
			}
			hiddenY[j]+=hiddenBias[j];
			hiddenY[j]=sigmoid(hiddenY[j]);
		}
		// get the value of output node
		double[] outputY=new double[outputNodeNum];
		for(int j=0;j<outputNodeNum;j++) {
			outputY[j]=0;
			for(int k=0;k<hiddenNodeNum;k++) {
				outputY[j]+=hiddenY[k]*weightHidden2Output[k][j];
			}
			outputY[j]+=outputBias[j];
			outputY[j]=sigmoid(outputY[j]);
		}
		return outputY;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[][] inputs= {{1,0,0},{1,1,0},{0,0,0},{0,1,0},{1,0,1},{1,1,1},{0,0,1},{0,1,1}};
		double[][] outputs= {{-1},{1},{-1},{0},{-1},{0},{-1},{0}};
		NN nn = new NN(inputs, 3, 20, 1, outputs, 0.1);
		for(int i=0;i<inputs.length;i++)
		System.out.println(nn.predict(inputs[i])[0]);
	}

}
