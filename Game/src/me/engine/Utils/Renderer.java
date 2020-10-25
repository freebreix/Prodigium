package me.engine.Utils;

import java.io.File;
import java.util.Arrays;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL45;

import me.engine.Main;
import me.engine.Utils.Event.EventManager;

/**
 * @author Christian
 * The 2D Renderer for dynamic Rendering
 */
public class Renderer {

	public static final int MAXDRAW=18*1000;
	float[] vertecies;
	float[] txt;
	int vindex=0,tindex;
	private VertexBuffer v;
	static Matrix4f scale,projection;
	public Camera c;
	Shader s;
	
	public Renderer() {
		s=new Shader(new File(Main.dir.getAbsolutePath()+"\\Assets\\Shader\\std.frag"), new File(Main.dir.getAbsolutePath()+"\\Assets\\Shader\\std.vert"));
		vertecies=new float[MAXDRAW];
		txt=new float[MAXDRAW];
		c=new Camera();
		EventManager.register(c);
	}
	
	/**
	 * Adds an Rect to the RenderList
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param width The width of the Rect
	 * @param height The height of the Rect
	 * @param texid The TextureID
	 * @param frame The Frame of Animation
	 */
	public void renderRect(float x, float y, float width, float height, long texid,int frame) {
		float tx=Texture.getx(texid)+Texture.getdx(texid)*frame;
		float ty=Texture.gety(texid);
		float tx2=Texture.getx(texid)+Texture.getdx(texid)+Texture.getdx(texid)*frame;
		float ty2=Texture.gety(texid)+Texture.getdy(texid);
		int atlas=Texture.getatlas(texid);
		tx/=Main.getTex().msize;
		ty/=Main.getTex().msize;
		tx2/=Main.getTex().msize;
		ty2/=Main.getTex().msize;
		
		vertecies[vindex++]=x;
		vertecies[vindex++]=y+height;
		vertecies[vindex++]=1;
		txt[tindex++]=tx;
		txt[tindex++]=ty2;
		txt[tindex++]=atlas;
		
		vertecies[vindex++]=x;
		vertecies[vindex++]=y;
		vertecies[vindex++]=1;
		txt[tindex++]=tx;
		txt[tindex++]=ty;
		txt[tindex++]=atlas;
		
		vertecies[vindex++]=x+width;
		vertecies[vindex++]=y;
		vertecies[vindex++]=1;
		txt[tindex++]=tx2;
		txt[tindex++]=ty;
		txt[tindex++]=atlas;
		
		vertecies[vindex++]=x;
		vertecies[vindex++]=y+height;
		vertecies[vindex++]=1;
		txt[tindex++]=tx;
		txt[tindex++]=ty2;
		txt[tindex++]=atlas;
		
		vertecies[vindex++]=x+width;
		vertecies[vindex++]=y+height;
		vertecies[vindex++]=1;
		txt[tindex++]=tx2;
		txt[tindex++]=ty2;
		txt[tindex++]=atlas;
		
		vertecies[vindex++]=x+width;
		vertecies[vindex++]=y;
		vertecies[vindex++]=1;
		txt[tindex++]=tx2;
		txt[tindex++]=ty;
		txt[tindex++]=atlas;
		
		if(vindex>MAXDRAW-19)
			flush();
	}
	
	/**
	 * Renders All the Quads in one Drawcall
	 */
	public void flush() {
		if(vindex<18)
			return;
		if(v==null) {
			v=new VertexBuffer(false);
			v.createBuffer(vertecies, 0, 3);
			v.createBuffer(txt, 1, 3);
		}else {
			v.updateBuffer(vertecies, 0, 3);
			v.updateBuffer(txt, 1, 3);
		}
		s.bind();
		s.useUniform("projection", projection);
		s.useUniform("scale", scale);
		s.useUniform("u_Textures", 0, 1, 2, 3, 4, 5, 6);
		s.useUniform("u_Transform", c.translate);
		Main.getTex().bind();
		v.bind(0);
		v.bind(1);
		GL45.glDrawArrays(GL45.GL_TRIANGLES, 0, vindex);
		v.unbind();
		s.unbind();
		vindex=0;
		tindex=0;
	}
	
	/**
	 *@deprecated Use flush
	 * @since commit 31cdb613d836cf4423abd8ca8ff51d9b86e27c46
	 * @see {@link #flush()}
	 */
	@Deprecated
	public void render() {
		flush();
	}
	
	/**
	 * Destroys the Renderer
	 */
	public void destroy() {
		v.destroy();
		vertecies=null;
	}
	
	/**
	 * Clears the Transforms
	 */
	public static void clearTransform() {
		//TODO: Clear them more efficiently
		scale=new Matrix4f();
		projection=new Matrix4f();
	}
	
	/**
	 * Transforms the Geometry
	 * Use the Transform of {@link Camera#setP(me.engine.Utils.Camera.CameraPos)}
	 * @param x The x coordinate to be transformed
	 * @param y The y coordinate to be transformed
	 * @param z The z coordinate to be transformed
	 */
	public void transform(float x,float y,float z) {
		scale.translate(x, y, z);
	}
	
	/**
	 * Modifies the Orthographic Projection
	 * @param left 
	 * @param right 
	 * @param bottom 
	 * @param top 
	 * @param zNear The zClipping Plane
	 * @param zFar The zClipping Plane
	 */
	public void ortho(float left, float right, float bottom, float top, float zNear, float zFar) {
		projection.ortho(left, right, bottom, top, zNear, zFar);
	}
	
	/**
	 * Scales the Geometry
	 * @param x The x coordinate to be scaled
	 * @param y The y coordinate to be scaled
	 * @param z The z coordinate to be scaled
	 */
	public void scale(float x,float y,float z) {
		scale.scale(x, y, z);
	}
	
}