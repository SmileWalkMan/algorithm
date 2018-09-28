package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockCondition {

	List<String> plist = new ArrayList<String>();
	int mark;
	Lock lock = new ReentrantLock();
	Condition condition = lock.newCondition();

	public void produce(int mark) {
		try {
			Thread.sleep(100);
			lock.lock();
			plist.add("\"P" + mark + "\"");
			System.out.println(mark + " Produce>>" + plist.get(plist.size() - 1) + ": <list=" + plist.size() + ">");
			condition.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public void consume(int mark) {
		try {
			Thread.sleep(1000);
			lock.lock();
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

	public static void main(String[] args) {
		LockCondition obj = new LockCondition();
		for (int i = 0; i < 5; i++) {
			ProduceThread ct = new ProduceThread(obj, i);
			ct.start();
		}
		for (int i = 1; i < 10; i++) {
			ConsumeThread pt = new ConsumeThread(obj, i);
			pt.start();
		}

	}

}

class ProduceThread extends Thread {
	LockCondition obj;
	int mark;

	public ProduceThread(LockCondition obj, int mark) {
		this.obj = obj;
		this.mark = mark;
	}

	public void run() {
		while (true) {
			obj.produce(mark);
		}
	}
}

class ConsumeThread extends Thread {
	LockCondition obj;

	int mark;

	public ConsumeThread(LockCondition obj, int mark) {
		this.obj = obj;
		this.mark = mark;
	}

	public void run() {
		while (true) {
			obj.consume(mark);
		}
	}
}
