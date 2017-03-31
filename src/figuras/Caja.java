package figuras;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Vector3f;
import simulador.Figura;
import simulador.Juego;
import utilidades.DibujarCollisionShape;

/**
 *
 * @author
 * Enrique Rios Santos
 */
public class Caja extends Figura{
    
    public Caja(float tam, BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego) {
        super(conjunto, listaObjetosFisicos, juego, false);
        
        //Creando una apariencia
        Appearance apariencia = new Appearance();
        Texture tex = new TextureLoader(System.getProperty("user.dir") + "//Texturas//madera.jpg", juego).getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        //Creacion de formas visuales y fisicas
        BranchGroup bg = new BranchGroup();
        Box cajaVisual = new Box(tam,tam,tam,Box.GENERATE_TEXTURE_COORDS,apariencia);
        BoxShape cajaFisica = new BoxShape(new Vector3f(tam,tam,tam));     
        DibujarCollisionShape.dibujarBoxShape(bg, cajaFisica);//dibujar figurafisica
        
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(cajaFisica);
        bg.addChild(cajaVisual);
        
        ramaVisible. addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(bg);
    }
    
    
    
}
