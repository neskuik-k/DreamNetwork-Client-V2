package be.alexandre01.dreamnetwork.api.config;

import be.alexandre01.dreamnetwork.api.utils.files.yaml.YamlFileUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Optional;

/*
 ↬   Made by Alexandre01Dev 😎
 ↬   done on 08/01/2024 at 15:38
*/
@Getter @Setter
public class WSSettings {
    private int port = 2352;
    private boolean wsEnabled = false;
    private boolean signed = true;
    private boolean localHostOnly = false;
    private String cryptMethod = "none";
    private String[] allowedHosts = new String[]{"*"};
    private String letsEncryptLocation = null;
    private String forceURL = null;
    @Getter static YamlFileUtils<WSSettings> yml;

    public static Optional<WSSettings> load(){
        yml = new YamlFileUtils<>(WSSettings.class);
        String[] randomString = // generate funny random string to make the file unique
                new String[]{"I'm a potato", "I'm a banana", "I'm a tomato", "I'm an apple", "I'm a pear", "I'm an orange", "I'm a lemon", "I'm a strawberry", "I'm a raspberry", "I'm a blueberry", "I'm a blackberry", "I'm a pineapple", "I'm a watermelon", "I'm a melon", "I'm a grape", "I'm a cherry", "I'm a peach", "I'm a plum", "I'm a apricot", "I'm a fig", "I'm a grapefruit", "I'm a kiwi", "I'm a mango", "I'm a papaya", "I'm a pomegranate", "I'm a quince", "I'm a tangerine", "I'm a avocado", "I'm a coconut", "I'm a date", "I'm a lychee", "I'm a nectarine", "I'm a olive", "I'm a passion fruit", "I'm a persimmon", "I'm a star fruit", "I'm a dragon fruit", "I'm a breadfruit", "I'm a guava", "I'm a jackfruit", "I'm a kumquat", "I'm a lime", "I'm a loquat", "I'm a mandarin", "I'm a cantaloupe", "I'm a honeydew", "I'm a plantain", "I'm a pomelo", "I'm a rambutan", "I'm a sapodilla", "I'm a tamarind", "I'm a ugli fruit", "I'm a yuzu", "I'm a zucchini", "I'm a cherry tomato", "I'm a cucumber", "I'm a eggplant", "I'm a olive", "I'm a pea", "I'm a pepper", "I'm a pumpkin", "I'm a squash", "I'm a tomato", "I'm a watermelon", "I'm a artichoke", "I'm a arugula", "I'm a asparagus", "I'm a beet", "I'm a broccoli", "I'm a brussels sprout", "I'm a cabbage", "I'm a carrot"
        };

        String random = randomString[(int) (Math.random() * randomString.length)];
        yml.addAnnotation("This is the global settings of the server | " + random);

        return yml.initStatic(new File(Config.getPath("data/WSSettings.yml")),true);
    }


    public void setMethod(Method method){
        this.cryptMethod = method.name().toLowerCase();
    }

    public Method getMethod(){
        return Method.valueOf(cryptMethod.toUpperCase());
    }
    @Getter
    public enum Method{
        NONE,
        LETSENCRYPT,
        CLOUDFLARE,
        CUSTOM,
        LOCALHOST,
        AUTO_SELF_SIGNED,
        TUNNEL;


        private Class<?> clazz;
        Method(){
            clazz = null;
        }

        Method(Class<?> clazz){
            this.clazz = clazz;
        }

        public <T> T getInstance(){
            try {
                return (T) clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void clear(){
        this.port = 2352;
        this.wsEnabled = true;
        this.signed = true;
        this.localHostOnly = false;
        this.cryptMethod = "none";
        this.allowedHosts = new String[]{"*"};
        this.letsEncryptLocation = null;
        this.forceURL = null;
    }
}
