package simulador;

import java.awt.*;
import javax.swing.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import figuras.Esfera;
import figuras.CamionPerseguidor;

import figuras.VehiculoPerseguidor;
import figuras.VehiculoPersona;
import figuras.VehiculoPolicia;
import utilidades.Escenario;
import utilidades.Suelo;

public class Juego extends JFrame implements Runnable {

    int estadoJuego = 0;
    SimpleUniverse universo;
    BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    public String rutaCarpetaProyecto = System.getProperty("user.dir") + "/";
    Thread hebra = new Thread(this);
    ArrayList<simulador.Figura> listaObjetosFisicos = new ArrayList<Figura>();
    ArrayList<simulador.Figura> listaObjetosNoFisicos = new ArrayList<Figura>();
    DiscreteDynamicsWorld mundoFisico;
    BranchGroup conjunto = new BranchGroup();
    public boolean actualizandoFisicas, mostrandoFisicas;
    public float tiempoJuego;
    // Pesonajes importantes del juego
    Figura personaje;
    Figura perseguidor;
    ArrayList<Vector3f> rastro = new ArrayList<Vector3f>();
    Figura curva1, curva2, curva3, curva4;
    ArrayList<Figura> curvasTrafico = new ArrayList<Figura>();
    CamionPerseguidor trafico1, trafico4;
    VehiculoPerseguidor trafico2, trafico3;
    ArrayList<Figura> trafico = new ArrayList<Figura>();

    public Juego() {
        CollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        AxisSweep3 broadphase = new AxisSweep3(worldAabbMin, worldAabbMax);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        mundoFisico = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        mundoFisico.setGravity(new Vector3f(0, -10, 0));

        Container GranPanel = getContentPane();
        Canvas3D zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        zonaDibujo.setPreferredSize(new Dimension(800, 600));
        GranPanel.add(zonaDibujo, BorderLayout.CENTER);
        universo = new SimpleUniverse(zonaDibujo);
        BranchGroup escena = crearEscena();

        //Mover cámara con el ratón:
        OrbitBehavior B = new OrbitBehavior(zonaDibujo);
        B.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        universo.getViewingPlatform().setViewPlatformBehavior(B);

        escena.compile();
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);

        hebra.start();
    }

    BranchGroup crearEscena() {
        BranchGroup objRoot = new BranchGroup();
        conjunto = new BranchGroup();

        objRoot.addChild(conjunto);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        DirectionalLight LuzDireccional = new DirectionalLight(new Color3f(10f, 10f, 10f), new Vector3f(1f, 0f, -1f));
        BoundingSphere limitesLuz = new BoundingSphere(new Point3d(-15, 10, 15), 100.0); //Localizacion de fuente/paso de luz
        objRoot.addChild(LuzDireccional);
        mostrar.setSchedulingBounds(limites);
        LuzDireccional.setInfluencingBounds(limitesLuz);
        Background bg = new Background();
        bg.setApplicationBounds(limites);
        bg.setColor(new Color3f(135f / 256, 206f / 256f, 250f / 256f));
        objRoot.addChild(bg);
        objRoot.addChild(mostrar);

        return objRoot;
    }

    void cargarContenido() {
        //Creando el personaje del juego, controlado por teclado. Tambien se pudo haber creado en CrearEscena()
        float masa = 5f;
        float radio = 1f;
        float posX = 9f;
        float posY = 1f, posZ = -6f;
        float elasticidad = 0.5f;
        float dumpingLineal = 0.5f;

        personaje = new VehiculoPersona(universo, conjunto, listaObjetosFisicos, this, true, limites);

        //la elasticidad es lo que hace que rebote contra el suelo.
        personaje.crearPropiedades(masa, elasticidad, 0.5f, posX, posY, posZ, mundoFisico);

        curva1 = new Esfera(0.6f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        curva1.crearPropiedades(0, elasticidad, dumpingLineal, 10,
                -2f, 85f, mundoFisico);

        curva2 = new Esfera(0.6f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        curva2.crearPropiedades(0, elasticidad, dumpingLineal, -70,
                -2f, 85f, mundoFisico);

        curva3 = new Esfera(0.6f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        curva3.crearPropiedades(0, elasticidad, dumpingLineal, -70,
                -2f, -85f, mundoFisico);

        curva4 = new Esfera(0.6f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        curva4.crearPropiedades(0, elasticidad, dumpingLineal, 10,
                -2f, -85f, mundoFisico);

        curvasTrafico.add(curva1);
        curvasTrafico.add(curva2);
        curvasTrafico.add(curva3);
        curvasTrafico.add(curva4);
        //trafico:
        trafico1 = new CamionPerseguidor(conjunto, listaObjetosFisicos, this, true);
        if (!actualizandoFisicas) {
            trafico1.crearPropiedades(3, 0.5f, 0.5f, 9, 1, 10, mundoFisico);
        }
        trafico1.cuerpoRigido.setLinearVelocity(new Vector3f(-2, 0, 0));
        trafico1.asignarObjetivo(curvasTrafico.get(0), 20f);

        trafico2 = new VehiculoPerseguidor(conjunto, listaObjetosFisicos, this, true, 1);
        if (!actualizandoFisicas) {
            trafico2.crearPropiedades(3, 0.5f, 0.5f, 0, 1, 90, mundoFisico);
        }
        trafico2.cuerpoRigido.setLinearVelocity(new Vector3f(-2, 0, 0));
        trafico2.asignarObjetivo(curvasTrafico.get(1), 20f);

        trafico3 = new VehiculoPerseguidor(conjunto, listaObjetosFisicos, this, true, 2);
        if (!actualizandoFisicas) {
            trafico3.crearPropiedades(3, 0.5f, 0.5f, -70, 1, 80, mundoFisico);
        }
        trafico3.cuerpoRigido.setLinearVelocity(new Vector3f(-2, 0, 0));
        trafico3.asignarObjetivo(curvasTrafico.get(2), 20f);

        trafico4 = new CamionPerseguidor(conjunto, listaObjetosFisicos, this, true);
        if (!actualizandoFisicas) {
            trafico4.crearPropiedades(3, 0.5f, 0.5f, -65, 1, -80, mundoFisico);
        }
        trafico4.cuerpoRigido.setLinearVelocity(new Vector3f(-2, 0, 0));
        trafico4.asignarObjetivo(curvasTrafico.get(3), 20f);

        trafico.add(trafico1);
        trafico.add(trafico2);
        trafico.add(trafico3);
        trafico.add(trafico4);

        perseguidor = new VehiculoPolicia(conjunto, listaObjetosFisicos, this, true);
        if (!actualizandoFisicas) {
            perseguidor.crearPropiedades(3, 0.5f, 0.5f, posX + 8.5f, posY, posZ + 40, mundoFisico);
        }
        perseguidor.cuerpoRigido.setLinearVelocity(new Vector3f(-2, 0, 0));

        float friccion = 0.5f;
        Suelo terreno = new Suelo(conjunto, mundoFisico, friccion);
        Escenario escenario = new Escenario(conjunto, listaObjetosFisicos, this, mundoFisico);

    }

    void actualizar(float dt) {
        //ACTUALIZAR EL ESTADO DEL JUEGO
        //trafico:
        for (Figura coche : trafico) {
            float posObj[] = coche.objetivo.posiciones;
            double distanciaObjetivoTrafico = Math.abs(posObj[0] - coche.posiciones[0])
                    + Math.abs(posObj[2] - coche.posiciones[2]);
            if (distanciaObjetivoTrafico < 1f) {
                if (coche.objetivo.equals(curvasTrafico.get(0))) {
                    coche.asignarObjetivo(curvasTrafico.get(1), 20);
                } else if (coche.objetivo.equals(curvasTrafico.get(1))) {
                    coche.asignarObjetivo(curvasTrafico.get(2), 20);
                } else if (coche.objetivo.equals(curvasTrafico.get(2))) {
                    coche.asignarObjetivo(curvasTrafico.get(3), 20);
                } else if (coche.objetivo.equals(curvasTrafico.get(3))) {
                    coche.asignarObjetivo(curvasTrafico.get(0), 20);
                }
            }
            coche.actualizar();
        }
        
        //perseguidor:
        if (perseguidor.activarPerseguir) {
            perseguidor.asignarObjetivo(rastro.get(0), 25f);
        }

        //ACTUALIZAR DATOS DE FUERZAS DEL PERSONAJE CONTROLADO POR EL JUGADOR
        if (personaje != null) {
            float fuerzaElevacion = 0, fuerzaLateral = 0;
            float velocidad;
            boolean movimiento = false;
            Vector3f velocidades = new Vector3f();
            personaje.cuerpoRigido.getLinearVelocity(velocidades);
            velocidad = velocidades.x + velocidades.y + velocidades.z;
            // System.out.println("Velocidad Coche: " + velocidad);
            //     System.out.println(System.getProperty("user.dir"));
            if (velocidad > 1 || velocidad < -1) {
                movimiento = true;
                if (!personaje.sonido.get(0).getEnable() && !personaje.frenar) {
                    personaje.sonido.get(0).setEnable(true);
                    personaje.sonido.get(1).setEnable(false);
                }
                personaje.sonido.get(0).setLoop(BackgroundSound.INFINITE_LOOPS);
            } else {
                personaje.sonido.get(0).setEnable(false);
            }

            if (personaje.frenar) {
                personaje.cuerpoRigido.setFriction(0.9f);
                personaje.cuerpoRigido.setDamping(0.9f, 0.99f);
                if (!personaje.sonido.get(1).getEnable() && (velocidad < -0.5 || velocidad > 0.5)) {
                    personaje.sonido.get(0).setEnable(false);
                    personaje.sonido.get(1).setEnable(true);
                } else if (velocidad > -0.5 && velocidad < 0.5) {
                    personaje.sonido.get(1).setEnable(false);
                }
                personaje.sonido.get(1).setLoop(BackgroundSound.INFINITE_LOOPS);
            } else if (personaje.adelante && personaje.derecha) {
                if (velocidad < 15) {
                    fuerzaElevacion = personaje.masa * 6f * 2.5f;
                }
                fuerzaLateral = -personaje.masa * 14f;

                personaje.cuerpoRigido.setFriction(0.5f);
                personaje.cuerpoRigido.setDamping(0.4f, 0.90f);

                personaje.corriendo = true;
            } else if (personaje.adelante && personaje.izquierda) {
                if (velocidad < 15) {
                    fuerzaElevacion = personaje.masa * 6f * 2.5f;
                }
                fuerzaLateral = personaje.masa * 14f;

                personaje.cuerpoRigido.setFriction(0.5f);
                personaje.cuerpoRigido.setDamping(0.4f, 0.90f);
                personaje.corriendo = true;
            } else if (personaje.atras && personaje.derecha) {
                if (velocidad > -15) {
                    fuerzaElevacion = -personaje.masa * 6f * 2.5f;
                }
                fuerzaLateral = personaje.masa * 14f;

                personaje.cuerpoRigido.setFriction(0.5f);
                personaje.cuerpoRigido.setDamping(0.4f, 0.90f);
                personaje.corriendo = true;
            } else if (personaje.atras && personaje.izquierda) {
                if (velocidad > -15) {
                    fuerzaElevacion = -personaje.masa * 6f * 2.5f;
                }
                fuerzaLateral = -personaje.masa * 14f;

                personaje.cuerpoRigido.setFriction(0.5f);
                personaje.cuerpoRigido.setDamping(0.4f, 0.90f);
                personaje.corriendo = true;
            } else if (personaje.adelante) {
                if (velocidad < 15) {
                    fuerzaElevacion = personaje.masa * 6f * 2.5f;
                }


                personaje.cuerpoRigido.setFriction(0.2f);
                personaje.cuerpoRigido.setDamping(0.2f, 0.90f);
                personaje.corriendo = true;
            } else if (personaje.atras) {
                if (velocidad > -15) {
                    fuerzaElevacion = -personaje.masa * 6f * 2.5f;
                }


                personaje.cuerpoRigido.setFriction(0.2f);
                personaje.cuerpoRigido.setDamping(0.2f, 0.99f);
                personaje.corriendo = true;
            } else if (personaje.derecha && movimiento) {
                if (velocidad > 1) {
                    fuerzaLateral = -personaje.masa * 14f;
                } else {
                    fuerzaLateral = personaje.masa * 14f;
                }

            } else if (personaje.izquierda && movimiento) {
                if (velocidad > 1) {
                    fuerzaLateral = personaje.masa * 14f;
                } else {
                    fuerzaLateral = -personaje.masa * 14f;
                }
            } else {
                personaje.corriendo = false;
                if (velocidad > -0.5 && velocidad < 0.5) {
                    personaje.sonido.get(0).setEnable(false);
                    personaje.sonido.get(1).setEnable(false);
                }
            }

            Vector3d direccionFrente = personaje.conseguirDireccionFrontal();
            personaje.cuerpoRigido.applyCentralForce(new Vector3f((float) direccionFrente.x * fuerzaElevacion * 0.1f, 0, (float) direccionFrente.z * fuerzaElevacion * 0.1f));
            personaje.cuerpoRigido.applyTorque(new Vector3f(0, fuerzaLateral, 0));

            //Método para que el perseguidor mire hacia el objetivo:
            if (perseguidor.activarPerseguir) {
                perseguidor.actualizar();
            } else {//comprobar que lo tiene cerca y velocidad excesiva
                Vector3f velocidadPersonaje = new Vector3f();
                personaje.cuerpoRigido.getLinearVelocity(velocidadPersonaje);
                float velocidadTotal = Math.abs(velocidadPersonaje.x) + Math.abs(velocidadPersonaje.z);

                Vector3f velocidadPerseguidor = new Vector3f();
                perseguidor.cuerpoRigido.getLinearVelocity(velocidadPerseguidor);
                float velocidadTotalP = Math.abs(velocidadPerseguidor.x) + Math.abs(velocidadPerseguidor.y);

                float distanciaAmbosX = Math.abs(perseguidor.posiciones[0]) - Math.abs(personaje.posiciones[0]);
                distanciaAmbosX = Math.abs(distanciaAmbosX);
                float distanciaAmbosZ = Math.abs(perseguidor.posiciones[2]) - Math.abs(personaje.posiciones[2]);
                distanciaAmbosZ = Math.abs(distanciaAmbosZ);

                System.out.println("Policia: " + velocidadTotal + "," + velocidadTotalP + "|" + distanciaAmbosX + distanciaAmbosZ);
                if ((velocidadTotal >= 14f || velocidadTotalP >= 3f) && (distanciaAmbosX + distanciaAmbosZ <= 10)) {
                    System.out.println("PERSECUCIÓN!");
                    /*
                     * -comprueba que la velocidad del personaje sea mayor que 15 o que haya colisionado con él (es decir,
                     * su velocidad pasaría de 0 a más de 10 por la colisión).
                     * -comprueba que la distancia entre ambos sea menor que 50.
                     */
                    rastro.add(new Vector3f(personaje.posiciones[0], personaje.posiciones[1] - 0.5f,
                            personaje.posiciones[2] - 3f));
                    perseguidor.activarPerseguir = true;
                    perseguidor.asignarObjetivo(rastro.get(0), 25f);

                    personaje.sonido.get(2).setEnable(true);
                    personaje.sonido.get(2).setLoop(BackgroundSound.INFINITE_LOOPS);
                }
            }

            Vector3d direccion2 = personaje.conseguirDireccionFrontal();
            
            //Dejar rastro:

            if (perseguidor.activarPerseguir) {
                Vector3f ultimoCheckpoint = rastro.get(rastro.size() - 1);
                double distanciaUltimaF = Math.abs(ultimoCheckpoint.x - personaje.posiciones[0])
                        + Math.abs(ultimoCheckpoint.z - personaje.posiciones[2]);
                if (distanciaUltimaF > 10f) {

                    Vector3f checkpoint = new Vector3f(personaje.posiciones[0],
                            personaje.posiciones[1] - 0.5f, personaje.posiciones[2] - 3f);
                    rastro.add(checkpoint);
                }
            }

            //comprobar si perseguidor llega a primer checkpoint de rastro:
            if (perseguidor.activarPerseguir) {
                Vector3f primerCheckpoint = rastro.get(0);
                double distanciaPrimerCheckpoint = Math.abs(primerCheckpoint.x - perseguidor.posiciones[0])
                        + Math.abs(primerCheckpoint.z - perseguidor.posiciones[2]);
                //System.out.println("distancia primer checkpoint: "+distanciaPrimerCheckpoint);
                if (distanciaPrimerCheckpoint < 1f) {

                    if (rastro.size()>1) {
                        perseguidor.asignarObjetivo(rastro.get(1), 25f);
                        rastro.remove(0);
                    }else {
                        System.out.println("Nulo, rastro size: "+rastro.size());
                        perseguidor.asignarObjetivo(personaje, 25f);

                        int confirmado = JOptionPane.showConfirmDialog(null, "El policia te ha pillado.\n ¿Quieres salir del juego?");
                        if (JOptionPane.OK_OPTION == confirmado) {
                            System.exit(0);
                        } else {
                            perseguidor.asignarObjetivo(new Vector3f(), 0);
                            perseguidor.activarPerseguir = false;
                            rastro = new ArrayList<Vector3f>();
                            personaje.sonido.get(2).setEnable(false);
                        }
                        
                         
                    }
                }
            }
            if (perseguidor.activarPerseguir) {
                if (rastro.size() > 10) {
                    perseguidor.asignarObjetivo(new Vector3f(), 0);
                    perseguidor.activarPerseguir = false;
                    rastro = new ArrayList<Vector3f>();
                    personaje.sonido.get(2).setEnable(false);
                }
            }
        }


        //ACTUALIZAR DATOS DE FUERZAS DE LAS FIGURAS AUTONOMAS  (ej. para que cada figura pueda persiguir su objetivo)
        for (int i = 0; i < this.listaObjetosFisicos.size(); i++)
            listaObjetosFisicos.get(i).actualizar();

        //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS FISICAS
        this.actualizandoFisicas = true;
        try {
            mundoFisico.stepSimulation(dt);    //mundoFisico.stepSimulation ( dt  ,50000, dt*0.2f);
        } catch (Exception e) {
            System.out.println("JBullet forzado. No debe crearPropiedades de solidoRigidos durante la actualizacion stepSimulation");
        }
        this.actualizandoFisicas = false;
        tiempoJuego = tiempoJuego + dt;
    }

    void mostrar() throws Exception {
        //MOSTRAR FIGURAS FISICAS (muestra el componente visual de la figura, con base en los datos de localizacion del componente fisico)
        this.mostrandoFisicas = true;
        try {
            if ((mundoFisico.getCollisionObjectArray().size() != 0) && (listaObjetosFisicos.size() != 0)) {
                for (int idFigura = 0; idFigura <= this.listaObjetosFisicos.size() - 1; idFigura++) {     // Actualizar posiciones fisicas y graficas de los objetos.
                    try {
                        int idFisico = listaObjetosFisicos.get(idFigura).identificadorFisico;
                        CollisionObject objeto = mundoFisico.getCollisionObjectArray().get(idFisico); //
                        RigidBody cuerpoRigido = RigidBody.upcast(objeto);
                        listaObjetosFisicos.get(idFigura).mostrar(cuerpoRigido);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Camara:
                    Point3d posicionCamara;
                    Point3d objetivoCamara;
                    if (personaje.camara) {
                        float cercania = 1f;
                        Vector3d direccion = personaje.conseguirDireccionFrontal();
                        posicionCamara = new Point3d(personaje.posiciones[0] - direccion.x * cercania,
                                personaje.posiciones[1] - direccion.y * cercania + 4f, personaje.posiciones[2] - direccion.z * cercania);

                        objetivoCamara = new Point3d(personaje.posiciones[0] + direccion.x,
                                personaje.posiciones[1] + direccion.y, personaje.posiciones[2] + direccion.z);

                        
                    } else {
                        Vector3d direccion = personaje.conseguirDireccionFrontal();
                        posicionCamara = new Point3d(personaje.posiciones[0] + direccion.x,
                                personaje.posiciones[1] + direccion.y + 50f, personaje.posiciones[2] + direccion.z);

                        objetivoCamara = new Point3d(personaje.posiciones[0] + direccion.x,
                                personaje.posiciones[1] + direccion.y, personaje.posiciones[2] + direccion.z);
                    }
                    colocarCamara(universo, posicionCamara, objetivoCamara);
                }
            }
        } catch (Exception e) {
        }
        this.mostrandoFisicas = false;
    }

    public void run() {
        cargarContenido();
        float dt = 3f / 100f;
        int tiempoDeEspera = (int) (dt * 1000);
        while (estadoJuego != -1) {

            try {
                actualizar(dt);
            } catch (Exception e) {
                System.out.println("Error durante actualizar. Estado del juego " + estadoJuego);
            }
            try {
                Thread.sleep(tiempoDeEspera);
            } catch (Exception e) {
            }
        }
    }

    void colocarCamara(SimpleUniverse universo, Point3d posicionCamara, Point3d objetivoCamara) {
        Transform3D datosConfiguracionCamara = new Transform3D();
        datosConfiguracionCamara.lookAt(posicionCamara, objetivoCamara, new Vector3d(0.001, 1.001, 0.001));
        try {
            datosConfiguracionCamara.invert();
            TransformGroup TGcamara = universo.getViewingPlatform().getViewPlatformTransform();
            TGcamara.setTransform(datosConfiguracionCamara);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        Juego x = new Juego();
        x.setTitle("Crazy Race Pursuit");
        x.setSize(800, 600);
        x.setVisible(true);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.colocarCamara(x.universo, new Point3d(8f, 10f, 25f), new Point3d(9, 0, 0));
    }
}
