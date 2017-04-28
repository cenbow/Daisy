package com.yourong.common.util;

/**
 *  LICENSE AND TRADEMARK NOTICES
 *
 *  Except where noted, sample source code written by Motorola Mobility Inc. and
 *  provided to you is licensed as described below.
 *
 *  Copyright (c) 2012, Motorola, Inc.
 *  All  rights reserved except as otherwise explicitly indicated.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 *  - Neither the name of Motorola, Inc. nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  INTERRUPTION) HOWEVER  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  POSSIBILITY OF SUCH DAMAGE.
 *
 *  Other source code displayed may be licensed under Apache License, Version
 *  2.
 *
 *  Copyright ¬© 2012, Android Open Source Project. All rights reserved unless
 *  otherwise explicitly indicated.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy
 *  of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0.
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 *
 */

// Please refer to the accompanying article at 
// http://developer.motorola.com/docs/using_the_advanced_encryption_standard_in_android/
// A tutorial guide to using AES encryption in Android
// First we generate a 256 bit secret key; then we use that secret key to AES encrypt a plaintext message.
// Finally we decrypt the ciphertext to get our original message back.
// We don't keep a copy of the secret key - we generate the secret key whenever it is needed, 
// so we must remember all the parameters needed to generate it -
// the salt, the IV, the human-friendly passphrase, all the algorithms and parameters to those algorithms.
// Peter van der Linden, April 15 2012

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;


public class AES {
    private static final String UTF_8 = "utf-8";
    /**
     * 日志对象
     */
    private static Logger Log = LoggerFactory.getLogger(AES.class);

    private final String KEY_GENERATION_ALG = "PBKDF2WithHmacSHA1";

    private final int HASH_ITERATIONS = 10000;
    private final int KEY_LENGTH = 256;
   private static char[] humanPassphrase =PropertiesUtil.getProperties("yourong.api.aes.key").toCharArray();
  //  private static char[] humanPassphrase ="1234567890abcdefghijklalkfdskkfj".toCharArray();

    private byte[] salt = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF};

    private PBEKeySpec myKeyspec = new PBEKeySpec(humanPassphrase, salt, HASH_ITERATIONS, KEY_LENGTH);
    private final String CIPHERMODEPADDING = "AES/CBC/PKCS5Padding";

    private SecretKeyFactory keyfactory = null;
    private SecretKey sk = null;
    private SecretKeySpec skforAES = null;
    private static byte[] iv = {0xA, 1, 0xB, 5, 4, 0xF, 7, 9, 0x17, 3, 1, 6, 8, 0xC, 0xD, 91};

    private IvParameterSpec IV;

    private AES() {
        try {
            keyfactory = SecretKeyFactory.getInstance(KEY_GENERATION_ALG);
            sk = keyfactory.generateSecret(myKeyspec);
        } catch (NoSuchAlgorithmException nsae) {
            Log.error("AESdemo", "no key factory support for PBEWITHSHAANDTWOFISH-CBC");
        } catch (InvalidKeySpecException ikse) {
            Log.error("AESdemo", "invalid key spec for PBEWITHSHAANDTWOFISH-CBC");
        }
        byte[] skAsByteArray = sk.getEncoded();
        skforAES = new SecretKeySpec(skAsByteArray, "AES");
        IV = new IvParameterSpec(iv);
    }

    private static AES aes = null;

    static {
        aes = new AES();
    }

    public static AES getInstance() {
        return aes;
    }

    /**
     *  加密
     * @param id 用户ID
     * @param token 令牌
     * @return
     */
    public String encryptToken(Long id,String token){
        StringBuffer s = new StringBuffer();
        try {
            s.append(id).append(split).append(token).append(split).append(DateUtils.getCurrentDate().getTime());
            return   encrypt(s.toString().getBytes(UTF_8));
        }catch (Exception e){
            Log.error(String.format("encryptToken: id= %s,token=%s",id,token),e);
        }
        return "";
    }
    private  static  String split = "|";

    public List<String>   tokenDecrypt(String ciphertext){
        String decrypt = decrypt(ciphertext);
        List<String> strings = Splitter.on(split).splitToList(decrypt);
        return strings;
    }

    public String encrypt(byte[] plaintext) {
        try {
            byte[] ciphertext = encrypt(CIPHERMODEPADDING, skforAES, IV, plaintext);
            String base64_ciphertext = Base64.encode(ciphertext);
            return base64_ciphertext;
        }catch (Exception e){
            Log.error("ase 加密失败",e);
        }
      return null;
    }

    public String decrypt(String ciphertext_base64) {
        byte[] s = Base64.decode(ciphertext_base64);
        String decrypted = new String(decrypt(CIPHERMODEPADDING, skforAES, IV, s));
        return decrypted;
    }


    private byte[] addPadding(byte[] plain) {
        byte plainpad[] = null;
        int shortage = 16 - (plain.length % 16);
        // if already an exact multiple of 16, need to add another block of 16
        // bytes
        if (shortage == 0)
            shortage = 16;

        // reallocate array bigger to be exact multiple, adding shortage bits.
        plainpad = new byte[plain.length + shortage];
        for (int i = 0; i < plain.length; i++) {
            plainpad[i] = plain[i];
        }
        for (int i = plain.length; i < plain.length + shortage; i++) {
            plainpad[i] = (byte) shortage;
        }
        return plainpad;
    }

    // Use this method if you want to remove the padding manually
    // This method removes the padding bytes
    private byte[] dropPadding(byte[] plainpad) {
        byte plain[] = null;
        int drop = plainpad[plainpad.length - 1]; // last byte gives number of
        // bytes to drop

        // reallocate array smaller, dropping the pad bytes.
        plain = new byte[plainpad.length - drop];
        for (int i = 0; i < plain.length; i++) {
            plain[i] = plainpad[i];
            plainpad[i] = 0; // don't keep a copy of the decrypt
        }
        return plain;
    }

    private byte[] encrypt(String cmp, SecretKey sk, IvParameterSpec IV,
                           byte[] msg) throws Exception {
        Cipher c = Cipher.getInstance(cmp);
        c.init(Cipher.ENCRYPT_MODE, sk, IV);
        return c.doFinal(msg);
    }

    private byte[] decrypt(String cmp, SecretKey sk, IvParameterSpec IV,
                           byte[] ciphertext) {
        try {
            Cipher c = Cipher.getInstance(cmp);
            c.init(Cipher.DECRYPT_MODE, sk, IV);
            return c.doFinal(ciphertext);
        } catch (NoSuchAlgorithmException nsae) {
            Log.error("AESdemo", "no cipher getinstance support for " + cmp);
        } catch (NoSuchPaddingException nspe) {
            Log.error("AESdemo", "no cipher getinstance support for padding " + cmp);
        } catch (InvalidKeyException e) {
            Log.error("AESdemo", "invalid key exception");
        } catch (InvalidAlgorithmParameterException e) {
            Log.error("AESdemo", "invalid algorithm parameter exception");
        } catch (IllegalBlockSizeException e) {
            Log.error("AESdemo", "illegal block size exception");
        } catch (BadPaddingException e) {
            Log.error("AESdemo", "bad padding exception");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String s = "神奇的AES";
        AES aes = AES.getInstance();
        System.out.println(aes.decrypt("PNfu2GurgUTmfiwRlgfuN6VblHhpIlAkgcxPDrOirhq1VXGNhaUHFsaB8vXhpOplaaV3L/Mdpc2yyU1AH16yZ4CDikYmJz3Z9H+2dLga3Pvs5LcXwXdPXZPMOncTqilU"));
    }

    /**
     * Turns array of bytes into string
     *
     * @param buf Array of bytes to convert to hex string
     * @return Generated hex string
     */
    public static String asHex(byte buf[]) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    
    /**
     * 周年庆活动加密
     * @param transationId
     * @param memberId
     * @param startTime
     * @param endTime
     * @return
     */
    public String encryptAnniversary(Long transactionId, Long memberId, Long startTime, Long endTime){
        StringBuffer s = new StringBuffer();
        try {
        	s.append(transactionId).append(split).append(memberId).append(split).append(startTime).append(split).append(endTime);
            return encrypt(s.toString().getBytes(UTF_8));
        }catch (Exception e){
            Log.error(String.format("encryptToken: id= %s,token=%s", transactionId, memberId),e);
        }
        return "";
    }
    
    /**
     * 
     * @Description:生产红包链接
     * @param activityId
     * @param sourceId
     * @return
     * @author: wangyanji
     * @time:2016年1月10日 下午2:59:53
     */
    public String encryptRedBag(Long activityId, Long sourceId) {
    	StringBuffer s = new StringBuffer();
        try {
        	s.append(activityId).append(split).append(sourceId);
            return encrypt(s.toString().getBytes(UTF_8));
        }catch (Exception e){
            Log.error("生产红包链接失败 activityId={}, sourceId={}", activityId, sourceId, e);
        }
        return "";
    }
}