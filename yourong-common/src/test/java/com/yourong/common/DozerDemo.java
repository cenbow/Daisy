package com.yourong.common;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.Mapping;
import org.junit.Test;

import com.yourong.common.util.BeanCopyUtil;

/**
 * 演示Dozer如何只要属性名相同，可以罔顾属性类型是基础类型<->String的差别，Array转为List，甚至完全另一种DTO的差别。
 * 并且能完美解决循环依赖问题。
 */
public class DozerDemo {
	
	//@Test
	public void testMapcopy(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("pname", "ssss");
		map.put("name", "ssssss");
		Product dto = BeanCopyUtil.map(map, Product.class);
		System.out.println(dto.getPname());
	}
	@Test
	public void testcopyMap(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("pname", "ssss");
		map.put("name", "ssssss");
		Product dto = new Product();
		dto.setName("sssssssssssssssssssssssssss");
		
		HashMap<?, ?> map2 = BeanCopyUtil.map(dto, HashMap.class);
				
		System.out.println(map2.get("pname"));
	}

	/**
	 * 从一个ProductDTO实例，创建出一个新的Product实例。
	 */
	//@Test
	public void map() {
		ProductDTO productDTO = new ProductDTO();
		productDTO.setName("car");
		productDTO.setPrice("200");

		PartDTO partDTO = new PartDTO();
		partDTO.setName("door");
		partDTO.setProduct(productDTO);

		productDTO.setParts(new PartDTO[] { partDTO });

		Product product = BeanCopyUtil.map(productDTO, Product.class);

		assertEquals("car", product.getName());
		// 原来的字符串被Map成Double。
		assertEquals(new Double(200), product.getPrice());
		// 原来的PartDTO同样被Map成Part。
		assertEquals("door", product.getParts().get(0).getName());
		// Part中循环依赖的Product同样被赋值。
		assertEquals("car", product.getParts().get(0).getProduct().getName());
		
		
		

	}

	/**
	 * 演示将一个ProductDTO实例的内容，Copy到另一个已存在的Product实例.
	 */
	//@Test
	public void copy() {
		ProductDTO productDTO = new ProductDTO();
		productDTO.setName("car");
		productDTO.setPrice("200");
		productDTO.setParentName("ssss");

		PartDTO partDTO = new PartDTO();
		partDTO.setName("door");
		partDTO.setProduct(productDTO);

		productDTO.setParts(new PartDTO[] { partDTO });

		// 已存在的Product实例
		Product product = new Product();
		product.setName("horse");
		product.setWeight(new Double(20));

		BeanCopyUtil.copy(productDTO, product);

		// 原来的20的属性被覆盖成200，同样被从字符串被专为Double。
		assertEquals(new Double(200), product.getPrice());
		System.out.println(product.getPname());
		assertEquals("ssss", product.getPname());
		
		// DTO中没有的属性值,在Product中被保留
		assertEquals(new Double(20), product.getWeight());
		// Part中循环依赖的Product同样被赋值。
		assertEquals("car", product.getParts().get(0).getProduct().getName());
	}

	public static class Product {
		private String name;
		private Double price;
		private List<Part> parts;
		// DTO中没有的属性
		private Double weight;
		
		private String pname;
		
		

		public String getPname() {
			return pname;
		}

		public void setPname(String pname) {
			this.pname = pname;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public List<Part> getParts() {
			return parts;
		}

		public void setParts(List<Part> parts) {
			this.parts = parts;
		}

		public Double getWeight() {
			return weight;
		}

		public void setWeight(Double weight) {
			this.weight = weight;
		}

	}

	public static class Part {
		// 反向依赖Product
		private Product product;

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Product getProduct() {
			return product;
		}

		public void setProduct(Product product) {
			this.product = product;
		}
	}

	public static class ProductDTO {
		@Mapping("pname")
		private String parentName;
		
		
		public String getParentName() {
			return parentName;
		}

		public void setParentName(String parentName) {
			this.parentName = parentName;
		}

		private String name;
		// 类型为String 而非 Double
		private String price;
		// 类型为Array而非List, PartDTO而非Part
		private PartDTO[] parts;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public PartDTO[] getParts() {
			return parts;
		}

		public void setParts(PartDTO[] parts) {
			this.parts = parts;
		}
	}

	public static class PartDTO {
		// 反向依赖ProductDTO
		private ProductDTO product;

		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ProductDTO getProduct() {
			return product;
		}

		public void setProduct(ProductDTO product) {
			this.product = product;
		}

	}
}