package com.study.nuwa.cloud.util;

import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RsaUtils {

    private final static String pubKeyPath = "/Users/wangtao/Develop/study/wt/nuwa/nuwa-cloud/nuwa-service/nuwa-service-system/src/main/resources/pub.txt";
    private final static String priKeyPath = "/Users/wangtao/Develop/study/wt/nuwa/nuwa-cloud/nuwa-service/nuwa-service-system/src/main/resources/pri.txt";

    /**
     * 从文件中读取公钥
     *
     * @return 公钥对象
     * @throws Exception
     */
    public static PublicKey getPublicKey() throws Exception {
        byte[] bytes = readFile(pubKeyPath);
        return getPublicKey(bytes);
    }

    /**
     * 从文件中读取密钥
     *
     * @return 私钥对象
     * @throws Exception
     */
    public static PrivateKey getPrivateKey() throws Exception {
        byte[] bytes = readFile(priKeyPath);
        return getPrivateKey(bytes);
    }

    /**
     * 获取公钥
     *
     * @param bytes 公钥的字节形式
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(byte[] bytes) throws Exception {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    /**
     * 获取密钥
     *
     * @param bytes 私钥的字节形式
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(byte[] bytes) throws Exception {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

    /**
     * 根据密文，生存rsa公钥和私钥,并写入指定文件
     *
     * @param publicKeyFilename  公钥文件路径
     * @param privateKeyFilename 私钥文件路径
     * @param secret             生成密钥的密文
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static void generateKey(String publicKeyFilename, String privateKeyFilename, String secret) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        SecureRandom secureRandom = new SecureRandom(secret.getBytes());
        keyPairGenerator.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        // 获取公钥并写出
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        writeFile(publicKeyFilename, publicKeyBytes);
        // 获取私钥并写出
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        writeFile(privateKeyFilename, privateKeyBytes);
    }


    private static byte[] readFile(String fileName) throws Exception {
        return Files.readAllBytes(new File(fileName).toPath());
    }


    private static void writeFile(String destPath, byte[] bytes) throws IOException {
        File dest = new File(destPath);
        if (!dest.exists()) {
            dest.createNewFile();
        }
        Files.write(dest.toPath(), bytes);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 测试RSAUtils工具类的使用
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String text = "123456";
        ///**************解密**************/
        //String rsaText = encryptByPublicKey(text);
        //System.out.println("加密：" + rsaText);
        //text = decryptByPrivateKey(rsaText);
        //System.out.println("解密：" + text);


        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String cryptPwd = passwordEncoder.encode(text);
        System.out.println(cryptPwd);
        //    {bcrypt}$2a$10$/nlEnCWg7p4T3ZjoBiHXeO//NroK9NupFfOWc8/5OvTtIPx0TGm2y
        //    {bcrypt}$2a$10$8ecKl8JVIgA39pknoixkjOC4FRz0CJwYItS7UU0Y5zOa0wZN45CqS
        //    {bcrypt}$2a$10$OrZQU6v/3UR0gY2cnPIT3eR2zm1r4wmHxuJEMZehXH9EcMZeC8CyG
    }

    /**
     * 公钥解密
     *
     * @param publicKeyString 公钥
     * @param text            待解密的信息
     * @return 解密后的文本
     */
    public static String decryptByPublicKey(String publicKeyString, String text) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(Base64.decodeBase64(text));
        return new String(result);
    }

    /**
     * 私钥加密
     *
     * @param privateKeyString 私钥
     * @param text             待加密的信息
     * @return 加密后的文本
     */
    public static String encryptByPrivateKey(String privateKeyString, String text) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.encodeBase64String(result);
    }

    /**
     * 私钥解密
     *
     * @param text 待解密文本
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String text) throws Exception {
        PrivateKey privateKey = RsaUtils.getPrivateKey();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(Base64.decodeBase64(text));
        return new String(result);
    }

    /**
     * 公钥加密
     *
     * @param text 待加密的文本
     * @return 加密后的文本
     */
    public static String encryptByPublicKey(String text) throws Exception {
        PublicKey publicKey = RsaUtils.getPublicKey();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.encodeBase64String(result);
    }

    /**
     * 构建RSA密钥对
     *
     * @return 生成后的公私钥信息
     */
    public static RsaUtils.RsaKeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = Base64.encodeBase64String(rsaPublicKey.getEncoded());
        String privateKeyString = Base64.encodeBase64String(rsaPrivateKey.getEncoded());
        return new RsaUtils.RsaKeyPair(publicKeyString, privateKeyString);
    }

    /**
     * RSA密钥对对象
     */
    public static class RsaKeyPair {
        private final String publicKey;
        private final String privateKey;

        public RsaKeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }


    /**
     * 只需调用一次 生成/打印新的公钥私钥  并测试是否可用
     * 控制台打印结果，解密成功 则将打印的公钥私钥重新赋值给工具类的 privateKey 、 publicKey
     *
     * @throws NoSuchAlgorithmException
     */
    //public static void printNewPubKeypriKey() {
    //    //调用 RsaUtils.generateKeyPair() 生成RSA公钥秘钥
    //    String tmpPriKey = "";//私钥
    //    String tmpPubKey = "";//公钥
    //    try {
    //        RsaUtils1.RsaKeyPair rsaKeyPair = RsaUtils1.generateKeyPair();
    //        tmpPriKey = rsaKeyPair.getPrivateKey();
    //        tmpPubKey = rsaKeyPair.getPublicKey();
    //        System.out.println("私钥：" + tmpPriKey);
    //        System.out.println("公钥：" + tmpPubKey);
    //    } catch (NoSuchAlgorithmException exception) {
    //        System.out.println("生成秘钥公钥失败");
    //    }
    //    //公钥加密、私钥解密
    //    try {
    //        String txt = "123456789,13000000001,oUpF8uMuAJO_M2pxb1Q9zNjWeS6oob1Q9zNjWeS6oQ9zNjW,1672914158,1672914158,啊";//注意需要加密的原文长度不要太长 过长的字符串会导致加解密失败
    //        System.out.println("加密前原文：" + txt);//加密后文本
    //        String rsaText = RsaUtils1.encryptByPublicKey(tmpPubKey, txt);//公钥加密 ！！！
    //        System.out.println("密文：" + rsaText);//加密后文本
    //        System.out.println("解密后原文：" + RsaUtils1.decryptByPrivateKey(tmpPriKey, rsaText));//私钥解密 ！！！
    //    } catch (BadPaddingException e) {
    //        System.out.println(e.getStackTrace());
    //        System.out.println("加解密失败");
    //    } catch (Exception e) {
    //        throw new RuntimeException(e);
    //    }
    //}
}
