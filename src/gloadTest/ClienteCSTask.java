package gloadTest;

import java.util.ArrayList;

import clientes.Cliente;
import uniandes.gload.core.Task;

public class ClienteCSTask extends Task {

	@Override
	public void fail() {
		// TODO Auto-generated method stub
		gen.fail();
		System.out.println(Task.MENSAJE_FAIL);
	}

	@Override
	public void success() {
		// TODO Auto-generated method stub
		System.out.println(Task.OK_MESSAGE);
	}

	@Override
	public void execute() {
		Cliente cl = new Cliente();
		try {
			cl.comenzarTransaccion(ip, id, puerto, a1, a2, a3);
			String temp = id + "," + cl.getVerDuration() + "," + cl.getConsultaDuration();
			tiempos.add(temp);
			success();

		} catch (Exception e) {
			e.printStackTrace();
			fail();

		}

	}

	String a1;
	String a2;
	String a3;

	String ip;

	int puerto;

	int id;

	private ArrayList<String> tiempos;

	Generator gen;

	public ClienteCSTask(String ip, int puerto, int id, String algo1, String algo2, String algo3,
			ArrayList<String> tiempos, Generator gen) {
		a1 = algo1;
		a2 = algo2;
		a3 = algo3;
		this.ip = ip;
		this.puerto = puerto;
		this.id = id;
		this.tiempos = tiempos;
		this.gen = gen;
	}

}
