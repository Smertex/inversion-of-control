package by.smertex.dependent;

import by.smertex.annotation.Component;
import by.smertex.annotation.Dependent;
import by.smertex.annotation.NotSingleton;

@Component
@NotSingleton
public class BoxForMusicBox {
    @Dependent(component = MusicBox.class)
    private MusicBox musicBox;

    public void playMusic(){
        System.out.println("Box for music box");
        musicBox.playMusic();
    }
}
