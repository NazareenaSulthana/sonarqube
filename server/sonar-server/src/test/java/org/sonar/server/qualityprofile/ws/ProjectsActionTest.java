/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.qualityprofile.ws;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.api.utils.System2;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.qualityprofile.QualityProfileDto;
import org.sonar.db.permission.GroupPermissionDto;
import org.sonar.db.user.RoleDao;
import org.sonar.db.user.UserDto;
import org.sonar.db.user.UserPermissionDto;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.qualityprofile.QProfileTesting;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.WsTester;
import org.sonar.server.ws.WsTester.TestRequest;

import static org.mockito.Mockito.mock;

public class ProjectsActionTest {

  @Rule
  public DbTester dbTester = DbTester.create(System2.INSTANCE);
  @Rule
  public UserSessionRule userSessionRule = UserSessionRule.standalone();

  private DbClient dbClient = dbTester.getDbClient();

  private DbSession session = dbTester.getSession();

  private QualityProfileDto xooP1;
  private QualityProfileDto xooP2;

  private ComponentDto project1;
  private ComponentDto project2;
  private ComponentDto project3;
  private ComponentDto project4;

  private Long userId = 42L;

  private RoleDao roleDao = dbClient.roleDao();

  private WsTester wsTester = new WsTester(new QProfilesWs(
    mock(RuleActivationActions.class),
    mock(BulkRuleActivationActions.class),
    new ProjectsAction(dbClient, userSessionRule)));

  @Before
  public void setUp() {
    userSessionRule.login("obiwan").setUserId(userId.intValue());
    dbClient.userDao()
      .insert(session, new UserDto()
        .setActive(true)
        .setId(userId)
        .setLogin("obiwan"));

    createProfiles();

    session.commit();
  }

  @Test
  public void should_list_authorized_projects_only() throws Exception {
    project1 = newProject("ABCD", "Project One");
    project2 = newProject("BCDE", "Project Two");
    dbClient.componentDao().insert(session, project1, project2);

    // user only sees project1
    roleDao.insertUserRole(session, new UserPermissionDto().setUserId(userId).setComponentId(project1.getId()).setPermission(UserRole.USER));

    associateProjectsWithProfile(session, xooP1, project1, project2);

    session.commit();

    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "selected").execute().assertJson(this.getClass(), "authorized_selected.json");
  }

  @Test
  public void should_paginate() throws Exception {
    project1 = newProject("ABCD", "Project One");
    project2 = newProject("BCDE", "Project Two");
    project3 = newProject("CDEF", "Project Three");
    project4 = newProject("DEFA", "Project Four");
    dbClient.componentDao().insert(session, project1, project2, project3, project4);

    addBrowsePermissionToAnyone(session, project1, project2, project3, project4);

    associateProjectsWithProfile(session, xooP1, project1, project2, project3, project4);

    session.commit();

    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "selected").setParam("pageSize", "2")
      .execute().assertJson(this.getClass(), "selected_page1.json");
    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "selected").setParam("pageSize", "2").setParam("page", "2")
      .execute().assertJson(this.getClass(), "selected_page2.json");
    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "selected").setParam("pageSize", "2").setParam("page", "3")
      .execute().assertJson(this.getClass(), "empty.json");
    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "selected").setParam("pageSize", "2").setParam("page", "4")
      .execute().assertJson(this.getClass(), "empty.json");

    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "selected").setParam("pageSize", "3").setParam("page", "1")
      .execute().assertJson(this.getClass(), "selected_ps3_page1.json");
    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "selected").setParam("pageSize", "3").setParam("page", "2")
      .execute().assertJson(this.getClass(), "selected_ps3_page2.json");
    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "selected").setParam("pageSize", "3").setParam("page", "3")
      .execute().assertJson(this.getClass(), "empty.json");
  }

  @Test
  public void should_show_unselected() throws Exception {
    project1 = newProject("ABCD", "Project One");
    project2 = newProject("BCDE", "Project Two");
    project3 = newProject("CDEF", "Project Three");
    project4 = newProject("DEFA", "Project Four");
    dbClient.componentDao().insert(session, project1, project2, project3, project4);

    addBrowsePermissionToAnyone(session, project1, project2, project3, project4);

    associateProjectsWithProfile(session, xooP1, project1, project2);

    session.commit();

    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "deselected").execute().assertJson(this.getClass(), "deselected.json");
  }

  @Test
  public void should_show_all() throws Exception {
    project1 = newProject("ABCD", "Project One");
    project2 = newProject("BCDE", "Project Two");
    project3 = newProject("CDEF", "Project Three");
    project4 = newProject("DEFA", "Project Four");
    dbClient.componentDao().insert(session, project1, project2, project3, project4);

    addBrowsePermissionToAnyone(session, project1, project2, project3, project4);

    associateProjectsWithProfile(session, xooP1, project1, project2);
    // project3 is associated with P2, must appear as not associated with xooP1
    associateProjectsWithProfile(session, xooP2, project3);

    session.commit();

    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "all").execute().assertJson(this.getClass(), "all.json");
  }

  @Test
  public void should_filter_on_name() throws Exception {
    project1 = newProject("ABCD", "Project One");
    project2 = newProject("BCDE", "Project Two");
    project3 = newProject("CDEF", "Project Three");
    project4 = newProject("DEFA", "Project Four");
    dbClient.componentDao().insert(session, project1, project2, project3, project4);

    addBrowsePermissionToAnyone(session, project1, project2, project3, project4);

    associateProjectsWithProfile(session, xooP1, project1, project2);

    session.commit();

    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "all").setParam("query", "project t").execute().assertJson(this.getClass(), "all_filtered.json");
  }

  @Test(expected = NotFoundException.class)
  public void should_fail_on_nonexistent_profile() throws Exception {
    newRequest().setParam("key", "unknown").setParam("selected", "all").execute();
  }

  @Test
  public void return_deprecated_uuid_field() throws Exception {
    project1 = newProject("ABCD", "Project One");
    project2 = newProject("BCDE", "Project Two");
    project3 = newProject("CDEF", "Project Three");
    project4 = newProject("DEFA", "Project Four");
    dbClient.componentDao().insert(session, project1, project2, project3, project4);

    addBrowsePermissionToAnyone(session, project1, project2, project3, project4);

    associateProjectsWithProfile(session, xooP1, project1, project2);
    // project3 is associated with P2, must appear as not associated with xooP1
    associateProjectsWithProfile(session, xooP2, project3);

    session.commit();

    newRequest().setParam("key", xooP1.getKey()).setParam("selected", "all").execute().assertJson(this.getClass(), "return_deprecated_uuid_field.json");
  }

  private void createProfiles() {
    xooP1 = QProfileTesting.newXooP1();
    xooP2 = QProfileTesting.newXooP2();
    dbClient.qualityProfileDao().insert(session, xooP1, xooP2);
  }

  private TestRequest newRequest() {
    return wsTester.newGetRequest("api/qualityprofiles", "projects");
  }

  private ComponentDto newProject(String uuid, String name) {
    return ComponentTesting.newProjectDto(uuid).setName(name);
  }

  private void addBrowsePermissionToAnyone(DbSession session, ComponentDto... projects) {
    for (ComponentDto project : projects) {
      roleDao.insertGroupRole(session, new GroupPermissionDto().setGroupId(null).setResourceId(project.getId()).setRole(UserRole.USER));
    }
  }

  private void associateProjectsWithProfile(DbSession session, QualityProfileDto profile, ComponentDto... projects) {
    for (ComponentDto project : projects) {
      dbClient.qualityProfileDao().insertProjectProfileAssociation(project.uuid(), profile.getKey(), session);
    }
  }
}
