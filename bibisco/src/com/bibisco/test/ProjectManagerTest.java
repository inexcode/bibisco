/*
 * Copyright (C) 2014-2015 Andrea Feccomandi
 *
 * Licensed under the terms of GNU GPL License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY. 
 * See the GNU General Public License for more details.
 * 
 */
package com.bibisco.test;

import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.bibisco.bean.ProjectDTO;
import com.bibisco.dao.client.ProjectMapper;
import com.bibisco.dao.client.ProjectsMapper;
import com.bibisco.dao.client.PropertiesMapper;
import com.bibisco.dao.model.Project;
import com.bibisco.dao.model.ProjectExample;
import com.bibisco.dao.model.Projects;
import com.bibisco.dao.model.ProjectsExample;
import com.bibisco.dao.model.Properties;
import com.bibisco.enums.TaskStatus;
import com.bibisco.manager.LocaleManager;
import com.bibisco.manager.ProjectManager;
import com.bibisco.manager.PropertiesManager;
import com.bibisco.manager.VersionManager;

public class ProjectManagerTest {

	@Test
	public void testIsProjectsDirectoryEmpty() {
		emptyProjectsDirectory();
		Assert.assertEquals(ProjectManager.isProjectsDirectoryEmpty(), true);
	}
	
	@Test
	public void testProjectsDirectoryExistsWithProjectsDirectoryEmpty() {
		emptyProjectsDirectory();
		Assert.assertEquals(ProjectManager.projectsDirectoryExists(), false);
	}
	
	@Test
	public void testProjectsDirectoryExistsWithWrongProjectsDirectory() {
		SqlSessionFactory lSqlSessionFactory = AllTests.getBibiscoSqlSessionFactory();
    	SqlSession lSqlSession = lSqlSessionFactory.openSession();
    	try {
			PropertiesMapper lPropertiesMapper = lSqlSession.getMapper(PropertiesMapper.class);
			Properties lProperties = new Properties();
			lProperties.setProperty("projectsDirectory");
			lProperties.setValue("C:/temp/bibiscotto/projects");
			lPropertiesMapper.updateByPrimaryKey(lProperties);	
			lSqlSession.commit();
    	} catch (Throwable t) {	
			lSqlSession.rollback();
    	} finally {
			lSqlSession.close();
		}
    	
    	PropertiesManager.getInstance().reload();
		Assert.assertEquals(ProjectManager.projectsDirectoryExists(), false);
	}
	
	@Test
	public void testProjectExists() {
		Assert.assertTrue(ProjectManager.projectExists(AllTests.TEST_PROJECT_ID));
	}
	
	@Test
	public void testProjectExistsWithInexistentProject() {
		Assert.assertFalse(ProjectManager.projectExists("XXX"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
	public void testProjectExistsWithNullIdProject() {
		ProjectManager.projectExists(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
	public void testProjectExistsWithEmptyIdProject() {
		ProjectManager.projectExists("");
	}
	
	@Test
	public void testProjectsDirectoryExistsWithCorrectProjectsDirectory() {
		Assert.assertTrue(ProjectManager.projectsDirectoryExists());
	}
	
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
	public void testSetProjectsDirectoryWithEmptyDirectory() {
		ProjectManager.setProjectsDirectory(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetProjectsDirectoryWithNonExistingDirectory() {
		ProjectManager.setProjectsDirectory("C:\\Users\\AndreaDocuments\\");
	}
	
	@Test
	public void testSetProjectsDirectory() {
		
		SqlSessionFactory lSqlSessionFactory = AllTests.getBibiscoSqlSessionFactory();
		SqlSession lSqlSession = lSqlSessionFactory.openSession();
		try {
			ProjectsMapper lProjectMapper = lSqlSession.getMapper(ProjectsMapper.class);
			lProjectMapper.deleteByExample(new ProjectsExample());
			lSqlSession.commit();
    	} finally {
			lSqlSession.close();
		}	
		
		ProjectManager.setProjectsDirectory("C:/temp/bibisco/projects");
		
    	lSqlSession = lSqlSessionFactory.openSession();
    	Properties lProperties;
    	try {
			PropertiesMapper lPropertiesMapper = lSqlSession.getMapper(PropertiesMapper.class);
			lProperties = lPropertiesMapper.selectByPrimaryKey("projectsDirectory");
    	} finally {
			lSqlSession.close();
		}
    	
    	Assert.assertEquals(lProperties.getValue(), "C:/temp/bibisco/projects");
	}
	
	@Test(expected = IllegalArgumentException.class)
	@edu.umd.cs.findbugs.annotations.SuppressWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
	public void testInsertProjectWithNullProjectDTO() {
		ProjectManager.insert(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInsertProjectWithEmptyName() {
		ProjectDTO lProjectDTO = new ProjectDTO();
		lProjectDTO.setLanguage(LocaleManager.getInstance().getLocale().getLanguage());
		lProjectDTO.setBibiscoVersion(VersionManager.getInstance().getVersion());
		ProjectManager.insert(lProjectDTO);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInsertProjectWithEmptyProjectsDirectory() {
		emptyProjectsDirectory();
		ProjectDTO lProjectDTO = new ProjectDTO();
		lProjectDTO.setLanguage(LocaleManager.getInstance().getLocale().getLanguage());
		lProjectDTO.setBibiscoVersion(VersionManager.getInstance().getVersion());
		ProjectManager.insert(lProjectDTO);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInsertProjectWithEmptyLanguage() {
		ProjectDTO lProjectDTO = new ProjectDTO();
		lProjectDTO.setName("Test 1");
		lProjectDTO.setBibiscoVersion(VersionManager.getInstance().getVersion());
		ProjectManager.insert(lProjectDTO);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInsertProjectWithEmptyBibiscoVersion() {
		ProjectDTO lProjectDTO = new ProjectDTO();
		lProjectDTO.setName("Test 1");
		lProjectDTO.setLanguage(LocaleManager.getInstance().getLocale().getLanguage());
		ProjectManager.insert(lProjectDTO);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInsertProjectAndProjectsDirectroryNotExists() {
		
		SqlSessionFactory lSqlSessionFactory = AllTests.getBibiscoSqlSessionFactory();
    	SqlSession lSqlSession = lSqlSessionFactory.openSession();
    	try {
			PropertiesMapper lPropertiesMapper = lSqlSession.getMapper(PropertiesMapper.class);
			Properties lProperties = new Properties();
			lProperties.setProperty("projectsDirectory");
			lProperties.setValue("C:/temp/bibiscotto/projects");
			lPropertiesMapper.updateByPrimaryKey(lProperties);	
			lSqlSession.commit();
    	} catch (Throwable t) {	
			lSqlSession.rollback();
    	} finally {
			lSqlSession.close();
		}
    	
    	PropertiesManager.getInstance().reload();
		
		ProjectDTO lProjectDTO = new ProjectDTO();
		lProjectDTO.setName("Test Insert");
		lProjectDTO.setLanguage(LocaleManager.getInstance().getLocale().getLanguage());
		lProjectDTO.setBibiscoVersion(VersionManager.getInstance().getVersion());
		lProjectDTO = ProjectManager.insert(lProjectDTO);
	}
	
	
	@Test
	public void testInsertProject() throws IOException {
		ProjectDTO lProjectDTO = new ProjectDTO();
		lProjectDTO.setName("Test Insert");
		lProjectDTO.setLanguage(LocaleManager.getInstance().getLocale().getLanguage());
		lProjectDTO.setBibiscoVersion(VersionManager.getInstance().getVersion());
		lProjectDTO = ProjectManager.insert(lProjectDTO);
		
		Assert.assertNotNull(lProjectDTO.getIdProject());
		Assert.assertEquals(lProjectDTO.getName(), "Test Insert");
		Assert.assertEquals(lProjectDTO.getLanguage(), LocaleManager.getInstance().getLocale().getLanguage());
		Assert.assertEquals(lProjectDTO.getBibiscoVersion(), VersionManager.getInstance().getVersion());
		Assert.assertNotNull(lProjectDTO.getArchitecture());
		Assert.assertEquals(lProjectDTO.getArchitecture().getPremiseTaskStatus(), TaskStatus.TODO);
		Assert.assertEquals(lProjectDTO.getArchitecture().getFabulaTaskStatus(), TaskStatus.TODO);
		Assert.assertEquals(lProjectDTO.getArchitecture().getStrandsTaskStatus(), TaskStatus.TODO);
		Assert.assertEquals(lProjectDTO.getArchitecture().getSettingTaskStatus(), TaskStatus.TODO);
		Assert.assertNull(lProjectDTO.getArchitecture().getStrandList());
		Assert.assertNull(lProjectDTO.getChapterList());
		Assert.assertNull(lProjectDTO.getLocationList());
		Assert.assertNull(lProjectDTO.getMainCharacterList());
		Assert.assertNull(lProjectDTO.getSecondaryCharacterList());
		
		SqlSessionFactory lSqlSessionFactory = AllTests.getProjectSqlSessionFactoryById(lProjectDTO.getIdProject());
		SqlSession lSqlSession = lSqlSessionFactory.openSession();
		List<Project> lListProject;
		try {
			ProjectMapper lProjectMapper = lSqlSession.getMapper(ProjectMapper.class);
			ProjectExample lProjectExample = new ProjectExample();
			lProjectExample.createCriteria()
			.andNameEqualTo("Test Insert")
			.andLanguageEqualTo(LocaleManager.getInstance().getLocale().getLanguage())
			.andBibiscoVersionEqualTo(VersionManager.getInstance().getVersion());
			lListProject = lProjectMapper.selectByExample(lProjectExample);			
    	} finally {
			lSqlSession.close();
		}	
		Assert.assertEquals(lListProject.size(), 1);
		
		lSqlSessionFactory = AllTests.getBibiscoSqlSessionFactory();
		lSqlSession = lSqlSessionFactory.openSession();
		List<Projects> lListProjects;
		try {
			ProjectsMapper lProjectsMapper = lSqlSession.getMapper(ProjectsMapper.class);
			ProjectsExample lProjectsExample = new ProjectsExample();
			lProjectsExample.createCriteria()
			.andIdProjectEqualTo(lProjectDTO.getIdProject())
			.andNameEqualTo("Test Insert");
			lListProjects = lProjectsMapper.selectByExample(lProjectsExample);			
    	} finally {
			lSqlSession.close();
		}	
		Assert.assertEquals(lListProjects.size(), 1);
	}
	
	@Before 
	@After
	public void init() throws ConfigurationException, IOException {
		
		SqlSessionFactory lSqlSessionFactory = AllTests.getBibiscoSqlSessionFactory();
    	SqlSession lSqlSession = lSqlSessionFactory.openSession();
    	try {
			PropertiesMapper lPropertiesMapper = lSqlSession.getMapper(PropertiesMapper.class);
			Properties lProperties = new Properties();
			lProperties.setProperty("projectsDirectory");
			lProperties.setValue("C:/temp/bibisco/projects");
			lPropertiesMapper.updateByPrimaryKey(lProperties);	
			lSqlSession.commit();
    	} catch (Throwable t) {	
			lSqlSession.rollback();
    	} finally {
			lSqlSession.close();
		}
    	
    	PropertiesManager.getInstance().reload();
    	
    	
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testImportProjectsFromProjectsDirectoryWithEmptyProjectsDirectory() {
		emptyProjectsDirectory();
		ProjectManager.importProjectsFromProjectsDirectory();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testImportProjectsFromProjectsDirectoryWithWrongProjectsDirectory() {
		
		SqlSessionFactory lSqlSessionFactory = AllTests.getBibiscoSqlSessionFactory();
    	SqlSession lSqlSession = lSqlSessionFactory.openSession();
    	try {
			PropertiesMapper lPropertiesMapper = lSqlSession.getMapper(PropertiesMapper.class);
			Properties lProperties = new Properties();
			lProperties.setProperty("projectsDirectory");
			lProperties.setValue("C:/temp/bibiscotto/projects");
			lPropertiesMapper.updateByPrimaryKey(lProperties);	
			lSqlSession.commit();
    	} catch (Throwable t) {	
			lSqlSession.rollback();
    	} finally {
			lSqlSession.close();
		}
    	
    	PropertiesManager.getInstance().reload();
		
		ProjectManager.importProjectsFromProjectsDirectory();
	}
	
	@Test
	public void testImportProjectsFromProjectsDirectory() throws ConfigurationException, IOException {
		
		SqlSessionFactory lSqlSessionFactory = AllTests.getBibiscoSqlSessionFactory();
		SqlSession lSqlSession = lSqlSessionFactory.openSession();
		try {
			ProjectsMapper lProjectMapper = lSqlSession.getMapper(ProjectsMapper.class);
			lProjectMapper.deleteByExample(new ProjectsExample());
			lSqlSession.commit();
    	} finally {
			lSqlSession.close();
		}	
		
		int lIntResult = ProjectManager.importProjectsFromProjectsDirectory();
		Assert.assertEquals(3, lIntResult);
		
		List<Projects> lListProjects;
		lSqlSession = lSqlSessionFactory.openSession();
		try {
			ProjectsMapper lProjectMapper = lSqlSession.getMapper(ProjectsMapper.class);
			ProjectsExample lProjectsExample = new ProjectsExample();
			lProjectsExample.setOrderByClause("id_project");
			lListProjects = lProjectMapper.selectByExample(lProjectsExample);
    	} finally {
			lSqlSession.close();
		}	
		
		Assert.assertEquals(AllTests.TEST_PROJECT_ID, lListProjects.get(0).getIdProject());
		Assert.assertEquals(AllTests.TEST_PROJECT2_ID, lListProjects.get(1).getIdProject());
		Assert.assertEquals(AllTests.TEST_PROJECT3_ID, lListProjects.get(2).getIdProject());
		
		lSqlSessionFactory = AllTests.getProjectSqlSessionFactoryById(AllTests.TEST_PROJECT_ID);
		lSqlSession = lSqlSessionFactory.openSession();
		Project lProject;
		try {
			ProjectMapper lProjectMapper = lSqlSession.getMapper(ProjectMapper.class);
			lProject = lProjectMapper.selectByPrimaryKey(AllTests.TEST_PROJECT_ID);
    	} finally {
			lSqlSession.close();
		}	
		Assert.assertEquals("1.3.0", lProject.getBibiscoVersion());
		
		lSqlSessionFactory = AllTests.getProjectSqlSessionFactoryById(AllTests.TEST_PROJECT2_ID);
		lSqlSession = lSqlSessionFactory.openSession();
		try {
			ProjectMapper lProjectMapper = lSqlSession.getMapper(ProjectMapper.class);
			lProject = lProjectMapper.selectByPrimaryKey(AllTests.TEST_PROJECT2_ID);
    	} finally {
			lSqlSession.close();
		}	
		Assert.assertEquals("1.3.0", lProject.getBibiscoVersion());
		
		lSqlSessionFactory = AllTests.getProjectSqlSessionFactoryById(AllTests.TEST_PROJECT3_ID);
		lSqlSession = lSqlSessionFactory.openSession();
		try {
			ProjectMapper lProjectMapper = lSqlSession.getMapper(ProjectMapper.class);
			lProject = lProjectMapper.selectByPrimaryKey(AllTests.TEST_PROJECT3_ID);
    	} finally {
			lSqlSession.close();
		}	
		Assert.assertEquals("1.3.0", lProject.getBibiscoVersion());
		
	}
	
	public void emptyProjectsDirectory() {
		
		SqlSessionFactory lSqlSessionFactory = AllTests.getBibiscoSqlSessionFactory();
    	SqlSession lSqlSession = lSqlSessionFactory.openSession();
    	try {
			PropertiesMapper lPropertiesMapper = lSqlSession.getMapper(PropertiesMapper.class);
			
			Properties lProperties = new Properties();
			lProperties.setProperty("projectsDirectory");
			lProperties.setValue("");
			lPropertiesMapper.updateByPrimaryKey(lProperties);
			
			lSqlSession.commit();
    	} catch (Throwable t) {	
			lSqlSession.rollback();
    	} finally {
			lSqlSession.close();
		}
    	
    	PropertiesManager.getInstance().reload();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLoadProjectWithNullProjectId() {
		ProjectManager.load(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLoadProjectWithEmptyProjectId() {
		ProjectManager.load("");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testLoadProjectWithWrongProjectId() {
		ProjectManager.load("xxx");
	}
	
	@Test
	public void testLoadProject() {
		ProjectDTO lProjectDTO = ProjectManager.load(AllTests.TEST_PROJECT_ID);
		Assert.assertEquals(AllTests.TEST_PROJECT_ID, lProjectDTO.getIdProject());
		Assert.assertEquals("1.3.0", lProjectDTO.getBibiscoVersion());
		Assert.assertEquals("Test", lProjectDTO.getName());
		Assert.assertEquals("en_US", lProjectDTO.getLanguage());
	}
}
