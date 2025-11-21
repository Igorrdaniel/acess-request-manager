package com.example.acess_request_manager.domain.module.model;

import static org.junit.jupiter.api.Assertions.*;

import com.example.acess_request_manager.domain.user.model.Department;
import org.junit.jupiter.api.Test;

class ModuleEntityTest {

    @Test
    void testCreateModule() {
        ModuleEntity module = new ModuleEntity();
        assertNotNull(module);
    }

    @Test
    void testSetAndGetName() {
        ModuleEntity module = new ModuleEntity();
        String name = "Test Module";
        module.setName(name);
        assertEquals(name, module.getName());
    }

    @Test
    void testSetAndGetDescription() {
        ModuleEntity module = new ModuleEntity();
        String description = "Test Description";
        module.setDescription(description);
        assertEquals(description, module.getDescription());
    }

    @Test
    void testActiveDefaultValue() {
        ModuleEntity module = new ModuleEntity();
        assertTrue(module.isActive());
    }

    @Test
    void testSetAndGetActive() {
        ModuleEntity module = new ModuleEntity();
        module.setActive(false);
        assertFalse(module.isActive());
    }

    @Test
    void testAddAllowedDepartment() {
        ModuleEntity module = new ModuleEntity();
        module.getAllowedDepartments().add(Department.TI);
        assertTrue(module.getAllowedDepartments().contains(Department.TI));
    }

    @Test
    void testAddIncompatibleModule() {
        ModuleEntity module = new ModuleEntity();
        ModuleEntity incompatibleModule = new ModuleEntity();
        module.getIncompatibleModules().add(incompatibleModule);
        assertTrue(module.getIncompatibleModules().contains(incompatibleModule));
    }
}
