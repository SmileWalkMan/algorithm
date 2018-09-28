package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FairLockCondition {

	List<String> plist = new ArrayList<String>();
	int mark;
	//公平锁
	Lock lock = new ReentrantLock(true);
	Condition condition = lock.newCondition();

	public void produceSignalAll(int mark) {
		try {
			
			lock.lock();
			Thread.sleep(1000);
			plist.add("\"P" + mark + "\"");
			System.out.println(mark + " Produce>>" + plist.get(plist.size() - 1) + ": <list=" + plist.size() + ">");
			condition.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void consumeAwaitAll(int mark) {
		try {
			
			lock.lock();
			Thread.sleep(100);
			while (plist.size() <= 0) {
				condition.await();
			}
			int index = plist.size() - 1;
			System.out.println(mark + ":Consume:" + plist.get(index) + ": Size=" + plist.size());
			plist.remove(index);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	////////////////////////////////////////
	public void produceSignalOne(int mark) {
		try {
			
			lock.lock();
			Thread.sleep(1000);
			plist.add("\"P" + mark + "\"");
			System.out.println(mark + " Produce>>" + plist.get(plist.size() - 1) + ": <list=" + plist.size() + ">");
			//只唤醒一个，因此只有一个从await之后运行，其他依旧处在await状态
			condition.signal();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void consumeAwaitOne(int mark) {
		try {
			
			lock.lock();
			Thread.sleep(100);
//			while (plist.size() <= 0) {
//				condition.await();
//			}
			//只有一个从await状态唤醒过来，其他都没有
			if(plist.size() <= 0) {
				condition.await();
			}
			int index = plist.size() - 1;
			System.out.println(mark + ":Consume:" + plist.get(index) + ": Size=" + plist.size());
			plist.remove(index);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	/////////////////////////////////////
	
	public static void main(String[] args) {
		FairLockCondition obj = new FairLockCondition();
		for (int i = 0; i < 3; i++) {
			ProduceThread ct = obj.new ProduceThread(obj, i);
			ct.start();
		}
		for (int i = 1; i < 10; i++) {
			ConsumeThread pt = obj.new ConsumeThread(obj, i);
			pt.start();
		}
	}

	class ProduceThread extends Thread {
		FairLockCondition obj;
		int mark;

		public ProduceThread(FairLockCondition obj, int mark) {
			this.obj = obj;
			this.mark = mark;
		}

		public void run() {
			while (true) {
				obj.produceSignalAll(mark);
//				obj.produceSignalOne(mark);
			}
		}
	}

	class ConsumeThread extends Thread {
		FairLockCondition obj;

		int mark;

		public ConsumeThread(FairLockCondition obj, int mark) {
			this.obj = obj;
			this.mark = mark;
		}

		public void run() {
			while (true) {
				obj.consumeAwaitAll(mark);
//				obj.consumeAwaitOne(mark);
			}
		}
	}
}


