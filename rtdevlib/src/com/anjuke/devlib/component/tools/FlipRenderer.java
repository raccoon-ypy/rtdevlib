package com.anjuke.devlib.component.tools;

import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.View;

import com.anjuke.devlib.component.FlipView;

public class FlipRenderer implements GLSurfaceView.Renderer {

	private FlipView flipViewController;

	private FlipCards cards;

	private boolean created = false;

	private final LinkedList<Texture> postDestroyTextures = new LinkedList<Texture>();

	public FlipRenderer(FlipView flipViewController, FlipCards cards) {
		this.flipViewController = flipViewController;
		this.cards = cards;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		created = true;

		cards.invalidateTexture();
		flipViewController.sendMessage(FlipView.MSG_SURFACE_CREATED);

	}

	public static float[] light0Position = { 0, 0, 100f, 0f };

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		float fovy = 20f;
		float eyeZ = height / 2f / (float) Math.tan(Texture.d2r(fovy / 2));

		GLU.gluPerspective(gl, fovy, (float) width / (float) height, 0.5f, eyeZ
				+ height / 2);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		GLU.gluLookAt(gl, width / 2f, height / 2f, eyeZ, width / 2f,
				height / 2f, 0.0f, 0.0f, 1.0f, 0.0f);

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);

		float lightAmbient[] = new float[] { 3.5f, 3.5f, 3.5f, 1f };
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);

		light0Position = new float[] { 0, 0, eyeZ, 0f };
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light0Position, 0);

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (cards.isVisible() && cards.isFirstDrawFinished())
			gl.glClearColor(1f, 1f, 1f, 1f);
		else
			gl.glClearColor(0f, 0f, 0f, 0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		synchronized (postDestroyTextures) {
			for (Texture texture : postDestroyTextures) {
				texture.destroy(gl);
			}
			postDestroyTextures.clear();
		}

		cards.draw(this, gl);
	}

	public void postDestroyTexture(Texture texture) {
		synchronized (postDestroyTextures) {
			postDestroyTextures.add(texture);
		}
	}

	public void updateTexture(int frontIndex, View frontView, int backIndex,
			View backView) {
		if (created) {
			cards.reloadTexture(frontIndex, frontView, backIndex, backView);
			flipViewController.getSurfaceView().requestRender();
		}
	}

}