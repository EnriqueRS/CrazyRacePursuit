/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import com.bulletphysics.collision.shapes.BoxShape;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.LineArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author
 * Enrique Rios Santos
 */
public class DibujarCollisionShape {
    public static void dibujarBoxShape(BranchGroup conjunto, BoxShape figuraFisica){
        BranchGroup grupoEjes = new BranchGroup();
        
        for(int i=0;i<12;i++){
            Vector3f v3 = new Vector3f();
            Vector3f v4 = new Vector3f();
            figuraFisica.getEdge(i, v3, v4);
            LineArray la = new LineArray(2, LineArray.COORDINATES | LineArray.COLOR_3);
            la.setCoordinate(0, new Point3f(v3.x,v3.y,v3.z));
            la.setCoordinate(1, new Point3f(v4.x,v4.y,v4.z));
            Color3f rojo = new Color3f(1.0f, 0.0f, 0.0f);
            Color3f verde = new Color3f(0.0f, 1.0f, 0.0f);
            la.setColor(0, rojo);
            la.setColor(1, verde);
            grupoEjes.addChild(new Shape3D(la));
        }
        conjunto.addChild(grupoEjes);
    }
}
