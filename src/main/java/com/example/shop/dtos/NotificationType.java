package ParkingCard;
import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;
public class CipherUtils {

    // AES Encryption/Decryption (ï¿½ cï¿½ sn trong lp ca bn)
    public static void encryptAES(byte[] data, byte[] key, byte[] encryptedData) throws ISOException {
    try {
        // Thêm padding vào d liu nu cn thit
        byte[] paddedData = addPKCS7Padding(data, (short)16);

        // To Cipher và AESKey
        Cipher cipher = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, false);
        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        aesKey.setKey(key, (short) 0);

        // Khi to cipher vi ch  ENCRYPT (không cn IV trong ECB)
        cipher.init(aesKey, Cipher.MODE_ENCRYPT);

        // Thc hin mã hóa và lu kt qu vào encryptedData
        cipher.doFinal(paddedData, (short) 0, (short) paddedData.length, encryptedData, (short) 0);
    } catch (CryptoException e) {
        ISOException.throwIt(ISO7816.SW_UNKNOWN);
    }
}


   public static byte[] decryptAES(byte[] encryptedData, byte[] key) throws ISOException {
    try {
        // To Cipher và AESKey
        Cipher cipher = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, false);
        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        aesKey.setKey(key, (short) 0);

        // Khi to cipher vi ch  DECRYPT (không cn IV trong ECB)
        cipher.init(aesKey, Cipher.MODE_DECRYPT);

        // To mng  lu d liu gii mã
        byte[] decryptedData = new byte[encryptedData.length];

        // Gii mã d liu
        cipher.doFinal(encryptedData, (short) 0, (short) encryptedData.length, decryptedData, (short) 0);

        // Loi b padding sau khi gii mã
        return removePKCS7Padding(decryptedData, (short)16);
    } catch (CryptoException e) {
        ISOException.throwIt(ISO7816.SW_UNKNOWN);
        return null;
    }
}

    public static byte[] addPKCS7Padding(byte[] data, short blockSize) {
    short paddingLength = (short)( blockSize - (data.length % blockSize));
    byte[] paddedData = new byte[data.length + paddingLength];

    // Sao chép d liu gc vào mng mi
     Util.arrayCopy(data,(short) 0, paddedData,(short) 0,(short) data.length);

    // in padding (byte có giá tr là paddingLength)
    for (short i = (short)data.length; i < (short)paddedData.length; i++) {
        paddedData[i] = (byte) paddingLength;
    }

    return paddedData;
}

    public static byte[] removePKCS7Padding(byte[] data, short blockSize) {
    // Ly giá tr padding t byte cui cùng
    short paddingLength = data[data.length - 1];
    
    // Xác nh chiu dài d liu không có padding
    short dataLength = (short)(data.length - paddingLength);
    
    // To mng mi có kích thc bng d liu không có padding
    byte[] unpaddedData = new byte[dataLength];

    // Sao chép d liu t v trí u tiên n v trí d liu không có padding
     Util.arrayCopy(data, (short)0, unpaddedData, (short)0, (short)dataLength);

    return unpaddedData;
}

    
    // Sinh khï¿½a AES t mt mng byte (s dng MD5)
    public static byte[] generateKeyAes() {
        try {
        	byte[] input = random();
            MessageDigest md = MessageDigest.getInstance(MessageDigest.ALG_MD5, false);
            md.update(input, (short) 0, (short) input.length);
            byte[] hashBytes = new byte[md.getLength()];
            md.doFinal(input, (short) 0, (short) input.length, hashBytes, (short) 0);
            return hashBytes;
        } catch (Exception e) {
            ISOException.throwIt(ISO7816.SW_UNKNOWN); 
            return null; 
        }
    }

    // Hash PIN vi Salt (SHA-256 vi nhiu vï¿½ng lp)
    public static byte[] hashPin(byte[] rawPin, byte[] salt) {
        try {
            // To MessageDigest cho SHA-256
            MessageDigest md = MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);
            short iterations = 100;

            // Gp rawPin vï¿½ salt thï¿½nh mt mng
            byte[] combined = new byte[(short) (rawPin.length + salt.length)];
            Util.arrayCopy(rawPin, (short) 0, combined, (short) 0, (short) rawPin.length);
            Util.arrayCopy(salt, (short) 0, combined, (short) rawPin.length, (short) salt.length);

            byte[] hash = new byte[md.getLength()];
            md.doFinal(combined, (short) 0, (short) combined.length, hash, (short) 0);
            byte[] storage = new byte[hash.length];

            // Bm li nhiu ln
            for (short i = 0; i < iterations; i++) {
                Util.arrayCopy(hash, (short) 0, storage, (short) 0, (short) hash.length);
                md.doFinal(storage, (short) 0, (short) storage.length, hash, (short) 0);
            }

            return hash;  
        } catch (Exception e) {
            ISOException.throwIt(ISO7816.SW_UNKNOWN);
            return null;
        }
    }

    // **RSA - To cp khï¿½a RSA (private & public)**
    public static KeyPair generateRSAKeyPair() {
        try {
            KeyPair keyPair = new KeyPair(KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1024);  // RSA 2048 bit
            keyPair.genKeyPair();
            return keyPair;
        } catch (Exception e) {
            ISOException.throwIt(ISO7816.SW_UNKNOWN);
            return null;
        }
    }

    // **RSA - Mï¿½ hï¿½a d liu bng khï¿½a cï¿½ng khai**
    public static short encryptRSA(RSAPublicKey publicKey, byte[] data, byte[]encryptedData) throws ISOException {
    try {
        Cipher cipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
        cipher.init(publicKey, Cipher.MODE_ENCRYPT);
        short len = cipher.doFinal(data, (short) 0, (short) data.length, encryptedData, (short) 0);
        return len;
    } catch (Exception e) {
        ISOException.throwIt(ISO7816.SW_UNKNOWN);
        return 0;
    }
	}


   public static short decryptRSA(RSAPrivateKey privateKey, byte[] encryptedData, byte[] decryptedData) throws ISOException {
    try {
        // Khi to i tng Cipher cho RSA vi ch  PKCS1
        Cipher cipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
        cipher.init(privateKey, Cipher.MODE_DECRYPT);
        short len = cipher.doFinal(encryptedData, (short) 0, (short) encryptedData.length, decryptedData, (short) 0);
        return len;
    } catch (Exception e) {
        ISOException.throwIt(ISO7816.SW_UNKNOWN);
        return 0;
    }
	
}

    
    
    private static byte[] random() throws ISOException {
   
		byte[] input = new byte[16];
		RandomData randomData = RandomData.getInstance(RandomData.ALG_SECURE_RANDOM);
		randomData.generateData(input, (short) 0, (short) input.length);
		return input;
	}

    // **RSA - Ly modulus vï¿½ exponent ca khï¿½a cï¿½ng khai ( s dng bï¿½n ngoï¿½i)**
    // public static byte[] getPublicKeyModulusAndExponent(RSAPublicKey publicKey) throws ISOException {
        // try {
            // byte[] buffer = new byte[ (short)(publicKey.getModulusLength() + publicKey.getExponentLength())];
            // short offset = 0;

            // // Ly modulus ca khï¿½a cï¿½ng khai
            // short modulusLength = publicKey.getModulus(buffer, offset);
            // offset += modulusLength;

            // // Ly exponent ca khï¿½a cï¿½ng khai
            // short exponentLength = publicKey.getExponent(buffer, offset);

            // return buffer;
        // } catch (Exception e) {
            // ISOException.throwIt(ISO7816.SW_UNKNOWN);
            // return null;
        // }
    // }
}
