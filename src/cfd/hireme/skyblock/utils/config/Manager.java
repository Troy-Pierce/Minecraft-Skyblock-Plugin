/*
 *        derpySkyblock - Derpy00001 | Derpy#5247
 *        discord.gg/bQxBB89
 *        Hi! :)
 */

package cfd.hireme.skyblock.utils.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.IOUtils;

import cfd.hireme.skyblock.Skyblock;
import cfd.hireme.skyblock.utils.Console;

public class Manager {
	private Skyblock plugin = Skyblock.getPlugin(Skyblock.class);
	private File issf;
	private FileConfiguration iss;
	private File isplf;
	private FileConfiguration ispl;
	private File configpath = new File(plugin.getDataFolder().getAbsolutePath()+"\\Configurations");
	private File templpath = new File(plugin.getDataFolder().getAbsoluteFile()+"\\IslandTemplates");
	public Manager(){
		//Checks if \plugins\Skyblock\ exists
		if(!(plugin.getDataFolder().exists())) {
			plugin.getDataFolder().mkdir();
		}
		if(!(configpath.exists())) {
			configpath.mkdir();
		}
		if(!(templpath.exists())) {
			templpath.mkdir();
		}
		//Create vars for configs
		issf=new File(configpath, "islands.yml");
		isplf=new File(configpath, "islanders.yml");
		
		//Validity Check
		if(!(issf.exists())) {
			try {
				issf.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(!(isplf.exists())) {
			try {
				isplf.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//Load YAML
		iss=YamlConfiguration.loadConfiguration(issf);
		ispl=YamlConfiguration.loadConfiguration(isplf);
		
		//Create template worlds in template folder
		if(plugin.getConfig().getBoolean("templates.Default")) {
			File templatedefault = new File(templpath+"\\Default");
			if(!(templatedefault.exists())) {
				try {
					copyTemplates("Default");
				} catch (IOException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(plugin.getConfig().getBoolean("templates.Winter")) {
			File templatedefault = new File(templpath+"\\Winter");
			if(!(templatedefault.exists())) {
				try {
					copyTemplates("Winter");
				} catch (IOException | URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void copyTemplates(String type) throws IOException, URISyntaxException {
		Console.print("-------Installing template-------");
		Console.print("-------Type: "+type+"-------");
		File outpath = this.templpath;
		JarFile jar = new JarFile(Skyblock.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		Enumeration<JarEntry> entries = jar.entries();
		while(entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if(entry.getName().contains("resources/")&&entry.getName().contains(type)) {
				String newname = entry.getName().substring("src/me/derpy/skyblock/resources/".length());
				String[] split = newname.split("/");
				String news = "";
				for(String string : split) {
					if(!string.contains(".")) {
						news=news.concat("\\"+string);
					}
				}
				File file = new File(outpath.getPath()+"\\"+news);
				if(!file.exists()) {
					Console.print("Directory: "+file.getAbsolutePath());
					file.mkdirs();
					if(!file.isDirectory()) {
						file.delete();
					}
				}
				try {
					String[] display = entry.getName().split("/");
					Console.print("File: "+display[display.length-1]);
					InputStream in = Skyblock.getPlugin(Skyblock.class).getResource(entry.getName());
//					InputStream in = Skyblock.class.getResourceAsStream("src/resources/"+newname);
//					Console.print("Creating:"+outpath.getPath()+"\\"+newname);
//					new File(outpath.getPath()+"\\"+newname);
					File temp = new File(outpath.getPath()+"\\"+newname);
					File.createTempFile(outpath.getPath()+"\\"+newname, null);
					FileOutputStream out = new FileOutputStream(temp);
					IOUtils.copy(in, out);
					in.close();
					out.close();
				}catch(Exception e) {e.printStackTrace();}
			}
		}
		jar.close();
		Console.print("Installed: "+type);
	}
	public FileConfiguration getIslandSettings() {
		return iss;
	}
	public void saveIslandSettings(){
		try {
			iss.save(issf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public FileConfiguration getIslanderSettings() {
		return ispl;
	}
	public void saveIslanderSettings(){
		try {
			ispl.save(isplf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
