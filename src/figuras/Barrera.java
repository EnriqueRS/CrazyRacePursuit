package figuras;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.image.TextureLoader;
import java.util.ArrayList;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import simulador.Figura;
import simulador.Juego;
import utilidades.DibujarCollisionShape;

/**
 *
 * @author
 * Enrique Rios Santos
 */
public class Barrera extends Figura{
    private BoxShape barreraFisica;
    
    public Barrera(float tam, BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego) {
        super(conjunto, listaObjetosFisicos, juego, false);
        
        //Creando una apariencia
        Appearance apariencia = new Appearance();
        Texture tex = new TextureLoader(System.getProperty("user.dir") + "//Texturas//ladrillo.jpg", juego).getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);
        float grosor=1.2f;
        float altura=1.7f;

        //Creacion de formas visuales y fisicas
        BranchGroup bg = new BranchGroup();
        Box barreraVisual = new Box(grosor,altura,tam,Box.GENERATE_TEXTURE_COORDS,apariencia);
        barreraFisica = new BoxShape(new Vector3f(grosor,altura,tam));     
        DibujarCollisionShape.dibujarBoxShape(bg, barreraFisica);//dibujar figurafisica
        
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(barreraFisica);
        bg.addChild(barreraVisual);
        
        ramaVisible. addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(bg);
    }
    
    public void aplicarRotacion(float alpha,float masa, float elasticidad, float dumpingLineal, float posX, float posY, float posZ, DiscreteDynamicsWorld mundoFisico) {
        this.mundoFisico = mundoFisico;
        //Creaci?n de un cuerpoRigido (o RigidBody) con sus propiedades fisicas 
        this.masa = masa;
        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        groundTransform.origin.set(new Vector3f(posX, posY, posZ));
        Vector3f inerciaLocal = new Vector3f(0, 1, 0);
        AxisAngle4f rotY = new AxisAngle4f();
        rotY.set(new Vector3f(0, 1, 0), alpha);
        Quat4f mov = new Quat4f();
        mov.set(rotY);
        groundTransform.setRotation(mov);
        
        DefaultMotionState EstadoDeMovimiento = new DefaultMotionState(groundTransform);
        RigidBodyConstructionInfo InformacionCuerpoR = new RigidBodyConstructionInfo(masa, EstadoDeMovimiento, this.ramaFisica.getCollisionShape(), inerciaLocal);
        InformacionCuerpoR.restitution = elasticidad;

        cuerpoRigido = new RigidBody(InformacionCuerpoR);
        cuerpoRigido.setActivationState(RigidBody.DISABLE_DEACTIVATION);
        cuerpoRigido.setDamping(dumpingLineal, 0.1f);   //a?ade m?s (1) o menos  (0) "friccion del aire" al desplazarse/caer o rotar
        cuerpoRigido.setFriction(0.8f);
        //A?adiendo el cuerpoRigido al mundoFisico
        mundoFisico.addRigidBody(cuerpoRigido); // add the body to the dynamics world
        identificadorFisico = mundoFisico.getNumCollisionObjects() - 1;

        //A?adiendo objetoVisual asociado al grafo de escea y a la lista de objetos fisicos visibles y situandolo
        conjunto.addChild(ramaVisible);
        this.listaObjetosFisicos.add(this);
        identificadorFigura = listaObjetosFisicos.size() - 1;

        //Presentaci?n inicial de la  figura visual asociada al cuerpo rigido
        Transform3D inip = new Transform3D();
        inip.set(new Vector3f(posX, posY, posZ));
        desplazamientoFigura.setTransform(inip);

        //Actualizacion de posicion. La rotacion se empezar? a actualizar en el primer movimiento (ver final del metodo mostrar(rigidBody))
        this.posiciones[0] = posX;
        this.posiciones[1] = posY;
        this.posiciones[2] = posZ;        
    }
    
}
