/*
 * SonarQube
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
package org.sonarqube.ws.client.organizations;

import javax.annotation.Generated;

/**
 * This is part of the internal API.
 * This is a POST request.
 * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/organizations/create">Further information about this action online (including a response example)</a>
 * @since 6.2
 */
@Generated("sonar-ws-generator")
public class CreateRequest {

  private String avatar;
  private String description;
  private String key;
  private String name;
  private String url;

  /**
   * Example value: "https://www.foo.com/foo.png"
   */
  public CreateRequest setAvatar(String avatar) {
    this.avatar = avatar;
    return this;
  }

  public String getAvatar() {
    return avatar;
  }

  /**
   * Example value: "The Foo company produces quality software for Bar."
   */
  public CreateRequest setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getDescription() {
    return description;
  }

  /**
   * Example value: "foo-company"
   */
  public CreateRequest setKey(String key) {
    this.key = key;
    return this;
  }

  public String getKey() {
    return key;
  }

  /**
   * This is a mandatory parameter.
   * Example value: "Foo Company"
   */
  public CreateRequest setName(String name) {
    this.name = name;
    return this;
  }

  public String getName() {
    return name;
  }

  /**
   * Example value: "https://www.foo.com"
   */
  public CreateRequest setUrl(String url) {
    this.url = url;
    return this;
  }

  public String getUrl() {
    return url;
  }
}
