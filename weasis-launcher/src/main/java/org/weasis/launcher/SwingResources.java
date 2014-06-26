package org.weasis.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.UIManager;

public class SwingResources {

    static final String AND_MNEMONIC = "AndMnemonic"; //$NON-NLS-1$
    static final String TITLE_SUFFIX = ".titleAndMnemonic"; //$NON-NLS-1$
    static final String TEXT_SUFFIX = ".textAndMnemonic"; //$NON-NLS-1$

    static final String KEY_MNEMONIC = "Mnemonic"; //$NON-NLS-1$
    static final String KEY_MNEMONIC_INDEX = "DisplayedMnemonicIndex"; //$NON-NLS-1$
    static final String KEY_TEXT = "Text"; //$NON-NLS-1$

    static final String KEY_TITLE = "Title"; //$NON-NLS-1$

    /**
     * <code>TextAndMnemonicHashMap</code> stores swing resource strings. Many of strings can have a mnemonic. For
     * example: FileChooser.saveButton.textAndMnemonic=&Save For this case method get returns "Save" for the key
     * "FileChooser.saveButtonText" and mnemonic "S" for the key "FileChooser.saveButtonMnemonic"
     * 
     * There are several patterns for the text and mnemonic suffixes which are checked by the
     * <code>TextAndMnemonicHashMap</code> class. Patterns which are converted to the xxx.textAndMnemonic key:
     * (xxxNameText, xxxNameMnemonic) (xxxNameText, xxxMnemonic) (xxx.nameText, xxx.mnemonic) (xxxText, xxxMnemonic)
     * 
     * These patterns can have a mnemonic index in format (xxxDisplayedMnemonicIndex)
     * 
     * Pattern which is converted to the xxx.titleAndMnemonic key: (xxxTitle, xxxMnemonic)
     * 
     */
    public static void loadResources(String path) {
        InputStream inStream = WeasisLauncher.class.getResourceAsStream(path);
        if (inStream != null) {
            String version = System.getProperty("java.version"); //$NON-NLS-1$
            boolean v6 = version.startsWith("1.6"); //$NON-NLS-1$
            Properties swingDialogs = new Properties();
            try {
                swingDialogs.load(inStream);
            } catch (IOException e) {
                System.err.println("Cannot read swing translations: " + e); //$NON-NLS-1$
            } finally {
                FileUtil.safeClose(inStream);
            }

            for (Object key : swingDialogs.keySet()) {
                String stringKey = key.toString();
                String compositeKey = null;

                if (stringKey.endsWith(AND_MNEMONIC)) {
                    Object value = swingDialogs.get(key);
                    if (value != null) {
                        String text = value.toString();
                        String mnemonic = null;
                        int index = text.indexOf('&');
                        if (0 <= index && index < text.length() - 1) {
                            char c = text.charAt(index + 1);
                            mnemonic = Integer.toString(Character.toUpperCase(c));
                        }

                        if (stringKey.endsWith(TEXT_SUFFIX)) {
                            compositeKey = composeKey(stringKey, TEXT_SUFFIX.length(), KEY_TEXT);
                            UIManager.put(compositeKey, getTextFromProperty(text));
                            if (mnemonic != null) {
                                if (v6 && stringKey.startsWith("ColorChooser")) { //$NON-NLS-1$
                                    compositeKey = composeKey(stringKey, TEXT_SUFFIX.length(), "NameText"); //$NON-NLS-1$
                                    UIManager.put(compositeKey, getTextFromProperty(text));
                                }
                                compositeKey = composeKey(stringKey, TEXT_SUFFIX.length(), KEY_MNEMONIC);
                                UIManager.put(compositeKey, mnemonic);
                                compositeKey = composeKey(stringKey, TEXT_SUFFIX.length(), KEY_MNEMONIC_INDEX);
                                UIManager.put(compositeKey, Integer.toString(index));
                            }
                        } else if (stringKey.endsWith(TITLE_SUFFIX)) {
                            compositeKey = composeKey(stringKey, TITLE_SUFFIX.length(), KEY_TITLE);
                            UIManager.put(compositeKey, getTextFromProperty(text));
                            if (mnemonic != null) {
                                compositeKey = composeKey(stringKey, TITLE_SUFFIX.length(), KEY_MNEMONIC);
                                UIManager.put(compositeKey, mnemonic);
                                compositeKey = composeKey(stringKey, TITLE_SUFFIX.length(), KEY_MNEMONIC_INDEX);
                                UIManager.put(compositeKey, Integer.toString(index));
                            }
                        }
                    }
                } else {
                    UIManager.put(key, swingDialogs.get(key));
                }
            }
        }
    }

    static String composeKey(String key, int reduce, String sufix) {
        return key.substring(0, key.length() - reduce) + sufix;
    }

    static String getTextFromProperty(String text) {
        return text.replace("&", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    static String getMnemonicFromProperty(String text) {
        int index = text.indexOf('&');
        if (0 <= index && index < text.length() - 1) {
            char c = text.charAt(index + 1);
            return Integer.toString(Character.toUpperCase(c));
        }
        return null;
    }

}
