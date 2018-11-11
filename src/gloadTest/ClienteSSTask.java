package gloadTest;

import clientes.ClienteSS;
import uniandes.gload.core.Task;

public class ClienteSSTask extends Task{

	@Override
	public void fail() {
		// TODO Auto-generated method stub
		gen.fail();
		gen.end();
		//System.out.println(Task.MENSAJE_FAIL);
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		//System.out.println(Task.OK_MESSAGE);
	}

	@Override
	public void execute() {
		ClienteSS cl = new ClienteSS();
		try {
			cl.comenzarTransaccion(ip, id, puerto, a1, a2, a3);
			String temp = cl.getVerDuration() + "," + cl.getConsultaDuration();
			gen.printTime(temp);
			gen.end();
			success();

		} catch (Exception e) {
			e.printStackTrace();
			gen.printTime(null);
			fail();



		}

	}

	String a1;
	String a2;
	String a3;

	String ip;

	int puerto;

	int id;


	GeneratorSS gen;

	public ClienteSSTask(String ip, int puerto, int id, String algo1, String algo2, String algo3, GeneratorSS gen) {

		a1 = algo1;
		a2 = algo2;
		a3 = algo3;
		this.ip = ip;
		this.puerto = puerto;
		this.id = id;
		this.gen = gen;
	}

}
