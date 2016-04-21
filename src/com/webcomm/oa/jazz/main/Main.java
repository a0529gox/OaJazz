package com.webcomm.oa.jazz.main;

import com.webcomm.oa.jazz.execute.Executor;

public class Main {

	public static void main(String[] args) {
		if (args == null) {
			System.out.println("設輸入Workitem編號");
			return;
		}
		
		int[] ids = new int[args.length];
		for (int i = 0; i < args.length; i++) {
			boolean hasError = false;
			int id = 0;
			
			try {
				id = Integer.parseInt(args[i]);
			} catch (NumberFormatException e) {
				hasError = true;
			}
			
			if (hasError) {
				System.out.println("請輸入正確的Workitem編號.(" + args[i] + ")");
				return ;
			}
			ids[i] = id;
		}
		
		Executor exe = new com.webcomm.oa.jazz.execute.CsCombineExecutor();
//		Executor exe = new com.webcomm.oa.jazz.execute.CsSeparateExecutor();
		exe.execute(ids);
	}

}
