package xss2;

public class AllHalf {

	public static void main(String[] args) {
		//System.out.println(">".equals("��"));
		String name="����";
		byte[] bytes=name.getBytes();
		for(Byte b:bytes){
			System.out.println(b);
		}
	}

}
