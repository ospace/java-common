package indextable;

public class Foo {
	public final String name;
	public final Integer num;
	
	public Foo(String name, Integer num) {
		this.name = name;
		this.num = num;
	}
	
	@Override
	public String toString() {
		return String.format("{name:%s, num:%d}", name, num);
	}
}