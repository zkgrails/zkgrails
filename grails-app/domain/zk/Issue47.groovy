package zk

class Issue47 {

    String firstName
    Double age
    Date   dob

    // demonstrate 1-1
    User   user

    // demonstrate 1-M
    static hasMany = [comments: Comment]

    static constraints = {
        firstName(nullable: true)
        age(nullable: true)
        dob(nullable: true)
        user(nullable: true)
    }

}