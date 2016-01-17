package com.lxl.landroid.net.impl;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import com.lxl.landroid.net.core.AccessLinker;
import com.lxl.landroid.net.core.HttpAccess;
import com.lxl.landroid.net.core.Processer;
import com.lxl.landroid.net.core.utils.Access;

public class ExecutorProcesser implements Processer {

	private ExecutorService executorService;
	private int DEFAULT_THREAD_NUM = 3;
	private BlockingQueue<Access> requests;
	private boolean flag;
	private HttpAccess httpAccess;
	public ExecutorProcesser(int thread_num, HttpAccess httpAccess) {
		super();
		this.DEFAULT_THREAD_NUM = thread_num;
		this.httpAccess = httpAccess;
	}

    public ExecutorProcesser(HttpAccess httpAccess) {
        this.httpAccess = httpAccess;
    }

    @Override
	public void startProcess() {
		stopProcess();
		requests = new LinkedBlockingDeque<Access>();
		executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
		flag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    try {
                        executorService.execute(new AccessLinker(requests.take(),
                                httpAccess));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        continue;
                    }

                }
            }
        }).start();

	}

	@Override
	public void stopProcess() {
		flag=false;
		if (executorService != null)
			executorService.shutdown();
	}

	@Override
	public boolean addRequest(Access request) {
		if (!requests.contains(request)) {
			requests.add(request);
			return true;
		}
		return false;
	}

	@Override
	public boolean cancelRequest(Access request) {

		if (requests.contains(request)) {
            //TODO
		}

		return false;
	}

}
