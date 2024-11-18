// Crear usuario para la base de datos 'funkos'
db.createUser({
    user: 'yahya',
    pwd: 'yahya',
    roles: [
        {
            role: 'readWrite',
            db: 'funkos',
        },
    ],
});

// Cambiar a la base de datos 'funkos'
db = db.getSiblingDB('funkos');

// Crear la colección 'pedidos' y añadir documentos iniciales
db.createCollection('pedidos');

db.pedidos.insertMany([
    {
        _id: ObjectId('6536518de9b0d305f193b5ef'),
        idUsuario: 1,
        cliente: {
            nombreCompleto: 'Mohamed',
            email: 'moha@gmail.com',
            telefono: '+34695970883',
            direccion: {
                calle: 'calle real',
                numero: '1',
                ciudad: 'Yuncos',
                provincia: 'Toledo',
                pais: 'España',
                codigoPostal: '45210',
            },
        },
        lineasPedido: [
            {
                idProducto: 1,
                precioProducto: 15.00,
                cantidad: 1,
                total: 15.00,
            },
            {
                idProducto: 2,
                precioProducto: 15.00,
                cantidad: 2,
                total: 30.00,
            },
        ],
        createdAt: '2023-10-23T12:57:17.3411925',
        updatedAt: '2023-10-23T12:57:17.3411925',
        isDeleted: false,
        totalItems: 3,
        total: 51.97,
        _class: 'Pedido',
    },
]);
