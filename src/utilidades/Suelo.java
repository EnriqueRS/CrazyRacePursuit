/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;
import com.sun.j3d.utils.image.TextureLoader;
import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;

/**
 *
 * @author
 * Enrique Rios Santos
 */
public class Suelo extends BranchGroup {

    private final Appearance app = new Appearance();
    private final TransformGroup tg;
    private TriangleStripArray tsa = null;
    private DiscreteDynamicsWorld mundoDinamico;
    private float ancho = 300f;
    private float largo = 300f;
    private float friccion;
    
    public Suelo(BranchGroup conjunto, DiscreteDynamicsWorld md, float friccion) {
        mundoDinamico=md;
        this.friccion=friccion;
        this.setCapability(BranchGroup.ALLOW_DETACH);

        tsa = new TriangleStripArray(4,
                TriangleStripArray.COORDINATES
                | TriangleStripArray.TEXTURE_COORDINATE_2, new int[]{4});
        tsa.setCoordinate(0, new Point3f(ancho/2, 0f, -largo/2));
        tsa.setCoordinate(1, new Point3f(-ancho/2, 0f, -largo/2));
        tsa.setCoordinate(2, new Point3f(ancho/2, 0f, largo/2));
        tsa.setCoordinate(3, new Point3f(-ancho/2, 0f, largo/2));
        tsa.setTextureCoordinate(0, 0, new TexCoord2f(0.0f, 0.0f));
        tsa.setTextureCoordinate(0, 1, new TexCoord2f(1.0f, 0.0f));
        tsa.setTextureCoordinate(0, 2, new TexCoord2f(0.0f, 1.0f));
        tsa.setTextureCoordinate(0, 3, new TexCoord2f(1.0f, 1.0f));

        ColoringAttributes coloringAttributes = new ColoringAttributes();
        coloringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
        app.setColoringAttributes(coloringAttributes);
        app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);

        inicializarSuelo();
        
        Shape3D s3d = new Shape3D(tsa, app);
        s3d.setUserData("suelo");
        Transform3D t3Dterreno = new Transform3D();
        t3Dterreno.set(new Vector3f(0, 0, 0));
        tg=new TransformGroup(t3Dterreno);
        tg.addChild(s3d);
        setTexture();
        BranchGroup ramaTerreno = new BranchGroup();
        
        ramaTerreno.addChild(tg);
        conjunto.addChild(ramaTerreno);
    }
    
    private void inicializarSuelo(){
        Transform startTransform = new Transform();
        startTransform.setIdentity();
        startTransform.origin.set(new Vector3f(0, 0, 0));
        ObjectArrayList<Vector3f> vertices = new ObjectArrayList<Vector3f>();
        vertices.add(new Vector3f(ancho/2, 0, largo/2));
        vertices.add(new Vector3f(ancho/2, 0, -largo/2));
        vertices.add(new Vector3f(-ancho/2, 0, largo/2));
        vertices.add(new Vector3f(-ancho/2, 0, -largo/2));
        CollisionShape laminaFisica = new ConvexHullShape(vertices);
        Vector3f localInertia = new Vector3f(0f, 0f, 0f);
        laminaFisica.calculateLocalInertia(0, localInertia);
        DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
        RigidBodyConstructionInfo cInfo = new RigidBodyConstructionInfo(0, myMotionState, laminaFisica, localInertia);
        cInfo.restitution = 0.1f;//elasticidad;
        RigidBody body = new RigidBody(cInfo);
        body.setFriction(friccion);//friccion
        mundoDinamico.addRigidBody(body);
    }

    public void setScale(double escale) {
        Transform3D t3d = new Transform3D();
        tg.getTransform(t3d);
        t3d.setScale(escale);
        tg.setTransform(t3d);
        tsa.setTextureCoordinate(0, 0, new TexCoord2f(
                0.0f,
                0.0f));
        tsa.setTextureCoordinate(0, 1, new TexCoord2f(
                (float) (1.0f * escale),
                0.0f));
        tsa.setTextureCoordinate(0, 2, new TexCoord2f(
                0.0f,
                (float) (1.0f * escale)));
        tsa.setTextureCoordinate(0, 3, new TexCoord2f(
                (float) (1.0f * escale),
                (float) (1.0f * escale)));
    }

    public void setTexture() {
        TextureLoader tl = new TextureLoader(System.getProperty("user.dir") + "//Texturas/calzada.jpg", null);
        Texture texture = tl.getTexture();
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        app.setTexture(texture);
    }
}
