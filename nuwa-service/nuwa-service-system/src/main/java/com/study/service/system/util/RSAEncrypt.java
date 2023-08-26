package com.study.service.system.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAEncrypt {

	private final static String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCmJ7EUyGo8Z17S" +
			"ZRsGw3qdJCyFegvABVePFfAhSoTwAo7hvQyWnwcwuITLAsN0u7JTnvG7Vxo9/KZR" +
			"v/9aTmcLZBR38SuIP01f5PiX721WnxA0A4jJ1Rw6uw8MeVnTNEyVNiofzAfasgFy" +
			"TRX1yXvqgB4g8/86SRSm0NuJDMaZiJ5xsX5R3i8uOZH2VqwzIHQzDb76KOJHhV3u" +
			"b7pGZfSMJi2qfFqEd1ULivguoL5xgr9ZWZwVszm4V2hmCoabdYVfJM8fNFo+dKfn" +
			"87YHqJAjOERr0GLZxfmQDtPCG6FETC9Q83F4gN6qgSEuRZLp9Y+TbLTBypC6P0Yw" +
			"1K+UDOcFAgMBAAECggEBAJ4SkuCvzooK2eYV9EFaBbOsL4MI2lbjfiqNd3dkmtZv" +
			"yJkwLuK5BrEuSn+M8ICE9SGizF0rf7bLPzv1Ci1S+6uVmfeGBKFJgAYkIvEBAqdv" +
			"Co5Bcoh567JgNe/rHy7UyasG7cp0ZGCGCuRAPA0qk25EVuWI3B8KWdBeUQ2wjtyO" +
			"2WvcnLmwx1DYVRN0GdkZ0j4+7BhwV2rL5elM7G1JnmAE7nxbU5SNuqG7hR3xz+Yc" +
			"cknyzr+st8ynk9cZmk5GXzEdlKE3Fv/nWlCEfhaJ7GQTf1MdfCGJLbgOE6o7lD6c" +
			"1qiIq43TAGFySs3gHbX+jNbzBMXJ2PicaHnN5VbctuECgYEA1Zn+7UH4PAVF4gim" +
			"WpEZIXF0htL3b/DcqiqTd3yCeb32rjrXpVSWLU3dsNsCID7HsYp6tCVhi0lo7Yhk" +
			"ZxUbsJ/dN+tsb5jLg5lLOlQgmSwtkv0gQkx6nNPj7fInoABh0mKdvkadWSfMQycx" +
			"vFrLHxrU4fj1Zd+fLR1O037h8b0CgYEAxyK8t9qE6zEh4Q+P1fD2UKycy1DlKt2/" +
			"Hymt/xsUMIsKSKVBUfOYWjebC3sfeYUyWH9ML7IoWnUgN48NNGdm3H9F/Yjycu09" +
			"6z9xQIKeA4d0pQvhO6JuLe00lbAo9g0eFpli04TNKfiqtU3wEroYoOgT3DmMygrx" +
			"OfnjWuH4iukCgYBYNIJwCTjv2CGWiInhhl+BTu+WqVGieS02W+SPh+v9R/Ow2P2p" +
			"+TcWuDvex2GmXSZra0rmTzVrpkHdLOCBLNdFZY6dg+tAXjFUQ54sFQdtGTAsrbh/" +
			"iENttZY+8StpyB6dGToYk+JwKZ4Q8QN5y2hrjNHyCrOTgPejUqH1dkTpZQKBgGMW" +
			"E9Vf9DebMQJt3eJUjhvCaBp63C0iPZFiCeQgM0GcTbesEv6WOT7a4FksIJM1BcIn" +
			"RV+ORKoOEcPTeHyU3wfkhI0NcaZiCideYBn7ZGjZI99kM+SahDxyBXJeP2N/T3Nr" +
			"S1N4hyXVSAwDBewrkUmvcQJp2HZIT5PLlaeJPUTRAoGBAM7r6KKU+Xr+/7IgBP9B" +
			"fdLJkuu+CF0pBT9vytxHckTgTFNrLOhYH4sISld4s5ygZRalKeFtTgFTFlsrJxyu" +
			"qbXQTuHybeWKEOkfkGpb4zWH0m207PdDBMEz8J1GnDP39V8TpLk84ztvFMPaVrIA" +
			"uHL/v/2h4qOavxiU/ZVC2pcl";

	private final static String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApiexFMhqPGde0mUbBsN6" +
			"nSQshXoLwAVXjxXwIUqE8AKO4b0Mlp8HMLiEywLDdLuyU57xu1caPfymUb//Wk5n" +
			"C2QUd/EriD9NX+T4l+9tVp8QNAOIydUcOrsPDHlZ0zRMlTYqH8wH2rIBck0V9cl7" +
			"6oAeIPP/OkkUptDbiQzGmYiecbF+Ud4vLjmR9lasMyB0Mw2++ijiR4Vd7m+6RmX0" +
			"jCYtqnxahHdVC4r4LqC+cYK/WVmcFbM5uFdoZgqGm3WFXyTPHzRaPnSn5/O2B6iQ" +
			"IzhEa9Bi2cX5kA7TwhuhREwvUPNxeIDeqoEhLkWS6fWPk2y0wcqQuj9GMNSvlAzn" +
			"BQIDAQAB";

	private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
	public static void main(String[] args) throws Exception {
		//生成公钥和私钥
		genKeyPair();
		//加密字符串
		String message = "810905";
		System.out.println("随机生成的公钥为:" + keyMap.get(0));
		System.out.println("随机生成的私钥为:" + keyMap.get(1));
		String messageEn = encrypt(message,keyMap.get(0));
		System.out.println();
		System.out.println(message + "\t加密后的字符串为:" + messageEn);
		String messageDe = decrypt(messageEn,keyMap.get(1));
		System.out.println("还原后的字符串为:" + messageDe);
	}

	/** 
	 * 随机生成密钥对 
	 * @throws NoSuchAlgorithmException 
	 */  
	public static void genKeyPair() throws NoSuchAlgorithmException {
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象  
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 初始化密钥对生成器，密钥大小为96-1024位  
		keyPairGen.initialize(1024,new SecureRandom());
		// 生成一个密钥对，保存在keyPair中  
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
		// 得到私钥字符串  
		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));  
		// 将公钥和私钥保存到Map
		keyMap.put(0,PUBLIC_KEY);  //0表示公钥
		keyMap.put(1,PRIVATE_KEY);  //1表示私钥
	}  
	/** 
	 * RSA公钥加密 
	 *  
	 * @param str 
	 *            加密字符串
	 * @param publicKey 
	 *            公钥 
	 * @return 密文 
	 * @throws Exception 
	 *             加密过程中的异常信息 
	 */  
	public static String encrypt( String str, String publicKey ) throws Exception{
		//base64编码的公钥
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		//RSA加密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
		return outStr;
	}

	/** 
	 * RSA私钥解密
	 *  
	 * @param str 
	 *            加密字符串
	 * @param privateKey 
	 *            私钥 
	 * @return 铭文
	 * @throws Exception 
	 *             解密过程中的异常信息 
	 */  
	public static String decrypt(String str, String privateKey) throws Exception{
		//64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
		//base64编码的私钥
		byte[] decoded = Base64.decodeBase64(privateKey);  
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
		//RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		String outStr = new String(cipher.doFinal(inputByte));
		return outStr;
	}
}

