package az.beenaport.authservice.enums;

public enum Roles {

    USER,

    ADMIN,

    MANAGER,

    COMMERCIAL_OWNER,

    OWNER,

    TENANT

    /*

    Admin
        └── Owner (müqavilə bağlayır Admin ilə)
            └── Property
                ├── Manager (Owner-in təyin etdiyi)
                │       └── Building → Floor → Unit → Tenant
                └── Commercial Owner (mağaza/ofis sahibi)
                        └── öz Unit-ini idarə edir

     */

}
