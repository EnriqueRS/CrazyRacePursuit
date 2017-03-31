package figuras;
import simulador.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import java.util.ArrayList;
import javax.media.j3d.*;
import javax.vecmath.*;
import net.sf.nwn.loader.AnimationBehavior;

public class CamionPerseguidor extends Figura {
    public Scene escenaPersonaje1;
    public AnimationBehavior ab = null;
    Vector3d direccion =new Vector3d(0,0,10);
    public String rutaCarpetaProyecto = System.getProperty("user.dir") + "/Texturas/";
    

   public CamionPerseguidor(BranchGroup conjunto,  ArrayList<Figura> listaObjetos, Juego juego, boolean esPe){
      super(conjunto,  listaObjetos, juego, true);
      esMDL=true;
      esPersonaje = esPe;
      
      //Creando una apariencia
     Appearance apariencia = new Appearance();
     TextureAttributes texAttr = new TextureAttributes();
     texAttr.setTextureMode(TextureAttributes.MODULATE);
     apariencia.setTextureAttributes(texAttr);

     //Creacion de la forma visual MDL
     TransformGroup figuraVisual =  crearObjetoMDL();
     BoxShape boxFisica = new BoxShape( new Vector3f( 1f, 1f, 2.5f));
     ramaFisica = new CollisionObject();
     ramaFisica.setCollisionShape(boxFisica);//figuraFisica
     ramaVisible. addChild(desplazamientoFigura);
     desplazamientoFigura.addChild(figuraVisual);

     //Creación de detector de teclas asociado a este cono
     if (esPersonaje){
        DeteccionControlPersonaje mueve = new DeteccionControlPersonaje(this);
        mueve.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0) );
        ramaVisible.addChild(mueve);
     }
   }

TransformGroup crearObjetoMDL() {
        BranchGroup objRootEscala = new BranchGroup();
        BranchGroup objRoot = new BranchGroup();


        //Escala:
        Transform3D escala = new Transform3D();
        escala.setScale(5.5f);
        TransformGroup objEscala = new TransformGroup(escala);
        objEscala.addChild(objRoot);
        objRootEscala.addChild(objEscala);

        //Ejes:
        LineArray ejeX = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
        LineArray ejeY = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
        ejeX.setCoordinate(0, new Point3f(-1.0f, 0.0f, 0.0f));
        ejeX.setCoordinate(1, new Point3f(1.0f, 0.0f, 0.0f));
        ejeY.setCoordinate(0, new Point3f(0.0f, -1.0f, 0.0f));
        ejeY.setCoordinate(1, new Point3f(0.0f, 1.0f, 0.0f));
        // les pone color 
        Color3f rojo = new Color3f(1.0f, 0.0f, 0.0f);
        Color3f verde = new Color3f(0.0f, 1.0f, 0.0f);
        ejeY.setColor(0, rojo);
        ejeY.setColor(1, verde);
        ejeX.setColor(0, rojo);
        ejeX.setColor(1, verde);
        //objRoot.addChild(new Shape3D(ejeX));
        //objRoot.addChild(new Shape3D(ejeY));

        //Apariencias:
        Appearance apariencia = new Appearance();

        ColoringAttributes colorCoche = new ColoringAttributes(0.95f, 0.95f, 0.95f, ColoringAttributes.FASTEST); 
        apariencia.setColoringAttributes(colorCoche);
        
        Appearance aparienciaRueda = new Appearance();
        ColoringAttributes colorNegro = new ColoringAttributes(0.2f, 0.2f, 0.2f, ColoringAttributes.FASTEST); 
        aparienciaRueda.setColoringAttributes(colorNegro);
        
        Appearance aparienciaCristal = new Appearance();
        ColoringAttributes colorAzul= new ColoringAttributes(0.5f, 0.8f, 0.6f, ColoringAttributes.FASTEST); 
        aparienciaCristal.setColoringAttributes(colorAzul);
        
        Appearance aparienciaLuz = new Appearance();
        ColoringAttributes colorAmarillo = new ColoringAttributes(1.00f, 1.00f, 0.5f, ColoringAttributes.FASTEST); 
        aparienciaLuz.setColoringAttributes(colorAmarillo);
        
        //Iluminaci�n:
        DirectionalLight luz = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(1.0f, 0.0f,-1.0f));
        luz.setInfluencingBounds(new BoundingSphere(new Point3d(-5, 0, 5), 100.0d));
        objRoot.addChild(luz);

        
        
        //Carroceria:
        Box carroceria = new Box(0.3f, 0.055f, 0.8f, apariencia);
        Transform3D translacionC1 = new Transform3D();
        translacionC1.set(new Vector3f(0,0.21f,0));
        TransformGroup translacionC1TG = new TransformGroup(translacionC1);
        translacionC1TG.addChild(carroceria);
        objRoot.addChild(translacionC1TG);
        
        Box carroceria2 = new Box(0.3f, 0.095f, 0.35f, apariencia);
        Transform3D translacionC2 = new Transform3D();
        translacionC2.set(new Vector3f(0,0.06f,0.0f));
        TransformGroup translacionC2TG = new TransformGroup(translacionC2);
        translacionC2TG.addChild(carroceria2);
        objRoot.addChild(translacionC2TG);
        
        Box techo = new Box(0.3f, 0.15f, 0.7f, apariencia);
        Box cristal1 = new Box(0.28f, 0.094f, 0.705f, aparienciaCristal);
        Box cristal2 = new Box(0.305f, 0.09f, 0.15f, aparienciaCristal);
        Box cristal3 = new Box(0.305f, 0.09f, 0.2f, aparienciaCristal);
        Transform3D translacionTecho = new Transform3D();
        translacionTecho.set(new Vector3f(0,0.35f,-0.1f));
        TransformGroup translacionTechoTG = new TransformGroup(translacionTecho);
        translacionTechoTG.addChild(techo);
        translacionTechoTG.addChild(cristal1);
        translacionTechoTG.addChild(cristal3);
        Transform3D translacionCristal2 = new Transform3D();
        translacionCristal2.set(new Vector3f(0,0,0.5f));
        TransformGroup translacionCristal2TG = new TransformGroup(translacionCristal2);
        translacionCristal2TG.addChild(cristal2);
        translacionTechoTG.addChild(translacionCristal2TG);
        objRoot.addChild(translacionTechoTG);
        
        Box paracocheD = new Box(0.3f, 0.095f, 0.08f, apariencia);
        Transform3D translacionpD = new Transform3D();
        translacionpD.set(new Vector3f(0,0.06f,0.75f));
        TransformGroup translacionpDTG = new TransformGroup(translacionpD);
        translacionpDTG.addChild(paracocheD);
        objRoot.addChild(translacionpDTG);
        
        Box paracocheT = new Box(0.3f, 0.095f, 0.08f, apariencia);
        Transform3D translacionTD = new Transform3D();
        translacionTD.set(new Vector3f(0,0.06f,-0.75f));
        TransformGroup translacionTTG = new TransformGroup(translacionTD);
        translacionTTG.addChild(paracocheT);
        objRoot.addChild(translacionTTG);
        
        //Ruedas:
        Cylinder ruedaD1 = new Cylinder(0.15f, 0.1f, aparienciaRueda);
        Cylinder ruedaD12 = new Cylinder(0.08f, 0.105f, apariencia);
        Transform3D rotaD1 = new Transform3D();
        rotaD1.rotZ(Math.PI/2f);
        TransformGroup rotaD1TG = new TransformGroup(rotaD1);
        rotaD1TG.addChild(ruedaD1);
        rotaD1TG.addChild(ruedaD12);
        Transform3D transladaD1 = new Transform3D();
        transladaD1.set(new Vector3f(-0.25f,0,0.51f));
        TransformGroup transladaD1TG = new TransformGroup(transladaD1);
        transladaD1TG.addChild(rotaD1TG);
        objRoot.addChild(transladaD1TG);
        
        Cylinder ruedaD2 = new Cylinder(0.15f, 0.1f, aparienciaRueda);
        Cylinder ruedaD22 = new Cylinder(0.08f, 0.105f, apariencia);
        Transform3D rotaD2 = new Transform3D();
        rotaD2.rotZ(Math.PI/2f);
        TransformGroup rotaD2TG = new TransformGroup(rotaD2);
        rotaD2TG.addChild(ruedaD2);
        rotaD2TG.addChild(ruedaD22);
        Transform3D transladaD2 = new Transform3D();
        transladaD2.set(new Vector3f(0.25f,0,0.51f));
        TransformGroup transladaD2TG = new TransformGroup(transladaD2);
        transladaD2TG.addChild(rotaD2TG);
        objRoot.addChild(transladaD2TG);

        Cylinder ruedaT1 = new Cylinder(0.15f, 0.1f, aparienciaRueda);
        Cylinder ruedaT12 = new Cylinder(0.08f, 0.105f, apariencia);
        Transform3D rotaT1 = new Transform3D();
        rotaT1.rotZ(Math.PI/2f);
        TransformGroup rotaT1TG = new TransformGroup(rotaT1);
        rotaT1TG.addChild(ruedaT1);
        rotaT1TG.addChild(ruedaT12);
        Transform3D transladaT1 = new Transform3D();
        transladaT1.set(new Vector3f(-0.25f,0,-0.51f));
        TransformGroup transladaT1TG = new TransformGroup(transladaT1);
        transladaT1TG.addChild(rotaT1TG);
        objRoot.addChild(transladaT1TG);
        
        Cylinder ruedaT2 = new Cylinder(0.15f, 0.1f, aparienciaRueda);
        Cylinder ruedaT22 = new Cylinder(0.08f, 0.105f, apariencia);
        Transform3D rotaT2 = new Transform3D();
        rotaT2.rotZ(Math.PI/2f);
        TransformGroup rotaT2TG = new TransformGroup(rotaT2);
        rotaT2TG.addChild(ruedaT2);
        rotaT2TG.addChild(ruedaT22);
        Transform3D transladaT2 = new Transform3D();
        transladaT2.set(new Vector3f(0.25f,0,-0.51f));
        TransformGroup transladaT2TG = new TransformGroup(transladaT2);
        transladaT2TG.addChild(rotaT2TG);
        objRoot.addChild(transladaT2TG);
        
        //Luces:
        Cylinder luzD1 = new Cylinder(0.04f, 0.02f, aparienciaLuz);
        Transform3D rotaLD1 = new Transform3D();
        rotaLD1.rotX(Math.PI/2f);
        TransformGroup rotaLD1TG = new TransformGroup(rotaLD1);
        rotaLD1TG.addChild(luzD1);
        Transform3D transladaLD1 = new Transform3D();
        transladaLD1.set(new Vector3f(-0.25f,0.2f,0.8f));
        TransformGroup transladaLD1TG = new TransformGroup(transladaLD1);
        transladaLD1TG.addChild(rotaLD1TG);
        objRoot.addChild(transladaLD1TG);
        
        Cylinder luzD2 = new Cylinder(0.04f, 0.02f, aparienciaLuz);
        Transform3D rotaLD2 = new Transform3D();
        rotaLD2.rotX(Math.PI/2f);
        TransformGroup rotaLD2TG = new TransformGroup(rotaLD2);
        rotaLD2TG.addChild(luzD2);
        Transform3D transladaLD2 = new Transform3D();
        transladaLD2.set(new Vector3f(0.25f,0.2f,0.8f));
        TransformGroup transladaLD2TG = new TransformGroup(transladaLD2);
        transladaLD2TG.addChild(rotaLD2TG);
        objRoot.addChild(transladaLD2TG);
        
        Box luzT1 = new Box(0.04f, 0.01f, 0.045f, aparienciaLuz);
        Transform3D rotaLT1 = new Transform3D();
        rotaLT1.rotX(Math.PI/2f);
        TransformGroup rotaLT1TG = new TransformGroup(rotaLT1);
        rotaLT1TG.addChild(luzT1);
        Transform3D transladaLT1 = new Transform3D();
        transladaLT1.set(new Vector3f(-0.25f,0.2f,-0.8f));
        TransformGroup transladaLT1TG = new TransformGroup(transladaLT1);
        transladaLT1TG.addChild(rotaLT1TG);
        objRoot.addChild(transladaLT1TG);
        
        Box luzT2 = new Box(0.04f, 0.01f, 0.045f, aparienciaLuz);
        Transform3D rotaLT2 = new Transform3D();
        rotaLT2.rotX(Math.PI/2f);
        TransformGroup rotaLT2TG = new TransformGroup(rotaLT2);
        rotaLT2TG.addChild(luzT2);
        Transform3D transladaLT2 = new Transform3D();
        transladaLT2.set(new Vector3f(0.25f,0.2f,-0.8f));
        TransformGroup transladaLT2TG = new TransformGroup(transladaLT2);
        transladaLT2TG.addChild(rotaLT2TG);
        objRoot.addChild(transladaLT2TG);

        TransformGroup cocheTG = new TransformGroup();
        cocheTG.addChild(objRootEscala);
        return cocheTG;
    }
}
